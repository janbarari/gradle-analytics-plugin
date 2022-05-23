package io.github.janbarari.gradle.analytics.domain.base

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class UseCase<INPUT, OUTPUT> {
    abstract fun execute(input: INPUT): OUTPUT
}
