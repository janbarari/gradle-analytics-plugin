package io.github.janbarari.gradle.bus.exception

class NotSerializableException : Exception() {
    override val message: String
        get() = "Your event is not serializable or parcelable object, to avoid memory leak and increase the performance you should make your event's serializable or parcelable!"
}
