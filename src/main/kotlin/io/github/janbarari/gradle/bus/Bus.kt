/**
 * MIT License
 * Copyright (c) 2022 Mehdi Janbarari (@janbarari)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.bus

import io.github.janbarari.gradle.bus.exception.NotSerializableException
import java.io.Serializable

/**
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
object Bus {

    /**
     * By default, any exception thrown in event receivers will be printed in the console
     * and won't crash the project build.
     *
     * In order to test the [io.github.janbarari.gradle.bus.Bus] this flag should be set to `True`.
     */
    private var isTestMode: Boolean = false

    @Volatile
    private var observers = ArrayList<Observer>()

    /**
     * Returns all the registered observers in [io.github.janbarari.gradle.bus.Bus].
     */
    @Synchronized
    fun getObservers(): ArrayList<Observer> = observers

    /**
     * Collects the unreferenced observers to be removed in the [io.github.janbarari.gradle.bus.Bus] drop cycle.
     */
    private var pendingDroppingObservers: ArrayList<Observer> = arrayListOf()

    /**
     * Enables test mode.
     */
    fun enableTestMode() {
        isTestMode = true
    }

    /**
     * Disables test mode
     */
    fun disableTestMode() {
        isTestMode = false
    }

    /**
     * Represents the test mode.
     */
    fun isTestMode(): Boolean {
        return isTestMode
    }

    /**
     * Posts the raw event to all observers.
     * @param event Event should be a serializable object.
     */
    fun post(event: Any) {
        post(null, event, null)
    }

    /**
     * Posts the raw event to a specific observer.
     * @param event Event should be a serializable object.
     * @param observerGUID Observer GUID.
     */
    fun post(event: Any, observerGUID: String) {
        post(observerGUID, event, null)
    }

    /**
     * Posts the raw event and sender information to a specific observer.
     * @param event event should be a serializable object.
     * @param observerGUID observer GUID.
     * @param sender sender class object.
     */
    fun post(event: Any, observerGUID: String, sender: Class<*>) {
        post(observerGUID, event, sender)
    }

    private fun <T : Any> post(observerGUID: String?, event: T, sender: Class<*>?) {
        validateEventType(event) {
            val observerIterator = getObservers().iterator()
            while (observerIterator.hasNext()) {
                val observer = observerIterator.next()
                if (observerGUID != null) {
                    if (observer.guid == observerGUID) {
                        postToObserver(observer, event, sender)
                    }
                } else {
                    postToObserver(observer, event, sender)
                }
            }
            dropObserversIfNeeded()
        }
    }

    /**
     * Register a new observer.
     * @param observerGUID every observer should have a GUID to unregister if needed.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> register(observerGUID: String, noinline unit: (T) -> Unit) {
        val iterator = getObservers().iterator()
        while (iterator.hasNext()) {
            val observer = iterator.next()
            if (observer.guid == observerGUID) {
                unregister(observerGUID)
                break
            }
        }
        getObservers().add(
            Observer(
                T::class.java, observerGUID, unit as (Any) -> Unit
            )
        )
    }

    /**
     * Register a new observer.
     *
     * Note: This observer only receives the event posted by a specific sender.
     *
     * @param observerGUID every observer should have a GUID to unregister if needed.
     * @param sender sender class object.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> register(
        observerGUID: String, sender: Class<*>, noinline unit: (T) -> Unit
    ) {
        val iterator = getObservers().iterator()
        while (iterator.hasNext()) {
            val observer = iterator.next()
            if (observer.guid == observerGUID) {
                unregister(observerGUID)
                break
            }
        }
        getObservers().add(
            Observer(
                T::class.java, observerGUID, unit as (Any) -> Unit, sender
            )
        )
    }

    /**
     * Unregister a single observer.
     * @param observerGUID observer GUID.
     */
    fun unregister(observerGUID: String) {
        val observerIterator = getObservers().iterator()
        while (observerIterator.hasNext()) {
            val observer = observerIterator.next()
            if (observer.guid == observerGUID) {
                observerIterator.remove()
            }
        }
    }

    /**
     * Unregister all observers.
     */
    fun unregisterAll() {
        getObservers().clear()
    }

    /**
     * Drops the unreferenced/crashed observers.
     */
    private fun dropObserversIfNeeded() {
        val iterator = pendingDroppingObservers.iterator()
        while (iterator.hasNext()) {
            val droppedObserver = iterator.next()
            val observerIterator = getObservers().iterator()
            while (observerIterator.hasNext()) {
                val observer = observerIterator.next()
                if (observer.guid == droppedObserver.guid) {
                    observerIterator.remove()
                }
            }
        }
        pendingDroppingObservers.clear()
    }

    /**
     * Checks the event is validated.
     * Step 1: Checks the event object is serializable.
     */
    private fun <T : Any> validateEventType(event: T, validated: () -> Unit) {
        if (event is Serializable) {
            validated()
        } else {
            throwException(NotSerializableException())
        }
    }

    /**
     * Exceptions will be thrown at debugging mode. And for preventing app crashes,
     * all exceptions will be just printed in stack trace at release mode.
     */
    private fun throwException(exception: Throwable) {
        if (isTestMode) {
            throw exception
        }
    }

    /**
     * Posts the event to the observers.
     */
    @Suppress(
        "TooGenericExceptionCaught",
        "SwallowedException"
    )
    private fun <T : Any> postToObserver(observer: Observer, event: T, sender: Class<*>?) {
        try {
            if (observer.sender != null && sender != null) {
                if (observer.sender == sender) {
                    observer.unit.invoke(event)
                }
            } else {
                if (sender == null && observer.sender != null) {
                    return
                }
                observer.unit.invoke(event)
            }
        } catch (e: Throwable) {
            throwException(e)
            pendingDroppingObservers.add(observer)
        }
    }

}
