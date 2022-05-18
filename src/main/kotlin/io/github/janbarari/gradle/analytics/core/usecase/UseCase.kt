package io.github.janbarari.gradle.analytics.core.usecase

abstract class UseCase<INPUT, OUTPUT> {
    abstract fun execute(input: INPUT): OUTPUT
}
