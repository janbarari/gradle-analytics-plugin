package io.github.janbarari.gradle.analytics.core

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class UseCaseNoOutput<INPUT> {
    abstract fun execute(input: INPUT)
}
