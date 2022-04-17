package io.github.janbarari.gradle.analytics.core.exception

class WrongEndTimeException : Throwable() {
    override val message: String
        get() = "BuildReport end-time can't be smaller than start-time."
}
