package io.github.janbarari.gradle.bus

import io.github.janbarari.gradle.bus.exception.NotSerializableException
import io.github.janbarari.gradle.bus.exception.SizeOutOfRangeException
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable

object Bus {

    @Volatile
    private var observers = ArrayList<Observer>()

    @Synchronized
    fun getObservers(): ArrayList<Observer> {
        return observers
    }

    private const val DEFAULT_POST_EVENT_LIMITATION_SIZE_IN_BYTES = 0
    private var postEventLimitationSizeInBytes: Int = DEFAULT_POST_EVENT_LIMITATION_SIZE_IN_BYTES
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
                        postWithThread(observer, event, sender)
                    }
                } else {
                    postWithThread(observer, event, sender)
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
        getObservers().forEach { observer ->
            if (observer.guid == observerGUID) {
                unregister(observerGUID)
                return@forEach
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
        getObservers().forEach { observer ->
            if (observer.guid == observerGUID) {
                unregister(observerGUID)
                return@forEach
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
     * Unregister all observers that KEvent notifies
     */
    fun unregisterAll() {
        getObservers().clear()
    }

    private fun dropObserversIfNeeded() {
        pendingDroppingObservers.forEach { droppedObserver ->
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
            if (postEventLimitationSizeInBytes > DEFAULT_POST_EVENT_LIMITATION_SIZE_IN_BYTES) {
                if (sizeOf(event) < postEventLimitationSizeInBytes) {
                    validated()
                } else {
                    throwException(SizeOutOfRangeException(postEventLimitationSizeInBytes))
                }
            } else {
                validated()
            }
        } else {
            throwException(NotSerializableException())
        }
    }

    private fun <T : Any> sizeOf(event: T): Int {
        return try {
            val byteOutputStream = ByteArrayOutputStream()
            val objectOutputStream = ObjectOutputStream(byteOutputStream)
            objectOutputStream.writeObject(event)
            objectOutputStream.flush()
            objectOutputStream.close()
            byteOutputStream.toByteArray().size
        } catch (e: Exception) {
            e.printStackTrace()
            DEFAULT_POST_EVENT_LIMITATION_SIZE_IN_BYTES
        }
    }

    /**
     * Exceptions will be throwing at debugging mode. And for prevent app crash,
     * all exceptions will be just print in stack trace at release mode
     */
    private fun throwException(exception: Exception) {
        exception.printStackTrace()
    }

    private fun <T : Any> postWithThread(observer: Observer, event: T, sender: Class<*>?) {
        if (observer.observerType == event::class.java) {
            postToObserver(observer, event, sender)
        }
    }

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
        } catch (e: Exception) {
            pendingDroppingObservers.add(observer)
        }
    }

}