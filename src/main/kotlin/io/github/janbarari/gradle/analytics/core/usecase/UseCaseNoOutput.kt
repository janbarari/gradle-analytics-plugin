package io.github.janbarari.gradle.analytics.core.usecase

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class UseCaseNoOutput<INPUT> {
    abstract fun execute(input: INPUT)
}
