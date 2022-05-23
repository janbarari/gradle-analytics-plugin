package io.github.janbarari.gradle.analytics.domain.base

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class UseCaseNoInput<OUTPUT> {
    abstract fun execute(): OUTPUT
}
