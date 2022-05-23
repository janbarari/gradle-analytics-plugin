package io.github.janbarari.gradle.analytics.domain.base

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class UseCaseNoOutput<INPUT> {
    abstract fun execute(input: INPUT)
}
