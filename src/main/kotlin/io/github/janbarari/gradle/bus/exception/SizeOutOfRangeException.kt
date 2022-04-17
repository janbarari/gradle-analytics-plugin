package io.github.janbarari.gradle.bus.exception

class SizeOutOfRangeException(private val eventLimitSize: Int) : Exception() {
    override val message: String?
        get() = "Only event with smaller than $eventLimitSize byte size can be transfer by KEvent"
}