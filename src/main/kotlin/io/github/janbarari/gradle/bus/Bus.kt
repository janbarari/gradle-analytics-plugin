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
import io.github.janbarari.gradle.bus.exception.SizeOutOfRangeException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.io.Serializable

object Bus {

    @Volatile
    private var observers = ArrayList<Observer>()

    @Synchronized
    fun getObservers(): ArrayList<Observer> = observers

    private const val DEFAULT_EVENT_SIZE_LIMIT_BYTES = -1
    private var postEventLimitationSizeInBytes: Int = DEFAULT_EVENT_SIZE_LIMIT_BYTES
    private var pendingDroppingObservers: ArrayList<Observer> = arrayListOf()

    fun setPostEventSizeLimitation(sizeInBytes: Int) {
        postEventLimitationSizeInBytes = sizeInBytes
    }

    fun post(event: Any) {
        post(null, event, null)
    }

    fun post(event: Any, observerGUID: String) {
        post(observerGUID, event, null)
    }

    inline fun <reified D> postWithSender(event: Any, observerGUID: String) {
        post(observerGUID, event, D::class.java)
    }

    fun <T : Any> post(observerGUID: String?, event: T, sender: Class<*>?) {
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
     * Add new observer
     * @param observerGUID every observer should have an GUID to unregister if needed
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

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T, reified D> registerWithSender(
        observerGUID: String, noinline unit: (T) -> Unit
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
                T::class.java, observerGUID, unit as (Any) -> Unit, D::class.java
            )
        )
    }

    /**
     * @param observerGUID Unregister the observer
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
     * Unregister all observers
     */
    fun unregisterAll() {
        getObservers().clear()
    }

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

    private fun <T : Any> validateEventType(event: T, validated: () -> Unit) {
        if (event is Serializable) {
            if (postEventLimitationSizeInBytes > DEFAULT_EVENT_SIZE_LIMIT_BYTES) {
                if (sizeOf(event) < postEventLimitationSizeInBytes) {
                    validated()
                }
            } else {
                validated()
            }
        } else {
            throwException(NotSerializableException())
        }
    }

    @Suppress("SwallowedException")
    private fun <T : Any> sizeOf(event: T): Int {
        return try {
            val byteOutputStream = ByteArrayOutputStream()
            val objectOutputStream = ObjectOutputStream(byteOutputStream)
            objectOutputStream.writeObject(event)
            objectOutputStream.flush()
            objectOutputStream.close()
            byteOutputStream.toByteArray().size
        } catch (e: IOException) {
            throw SizeOutOfRangeException(postEventLimitationSizeInBytes)
        }
    }

    /**
     * Exceptions will be throwing at debugging mode. And for prevent app crash,
     * all exceptions will be just print in stack trace at release mode
     */
    private fun throwException(exception: Exception) {
        exception.printStackTrace()
    }

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
            pendingDroppingObservers.add(observer)
        }
    }

}
