package io.github.janbarari.gradle.analytics.core

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class UseCase<INPUT, OUTPUT> {
    abstract fun execute(input: INPUT): OUTPUT
}
