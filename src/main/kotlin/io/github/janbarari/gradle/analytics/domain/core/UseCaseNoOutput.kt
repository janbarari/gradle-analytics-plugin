package io.github.janbarari.gradle.analytics.domain.core

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class UseCaseNoOutput<INPUT> {
    abstract fun execute(input: INPUT)
}
