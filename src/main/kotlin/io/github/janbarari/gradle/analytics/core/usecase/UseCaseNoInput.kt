package io.github.janbarari.gradle.analytics.core.usecase

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class UseCaseNoInput<OUTPUT> {
    abstract fun execute(): OUTPUT
}
