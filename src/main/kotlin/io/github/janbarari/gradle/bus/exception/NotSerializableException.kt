package io.github.janbarari.gradle.bus.exception

class NotSerializableException : Exception() {
    override val message: String
        get() = "Your event is not serializable, " +
                "to avoid memory leaks and increase the performance please make your event serializable."
}
