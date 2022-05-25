package io.github.janbarari.gradle.analytics.domain.core

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class UseCaseNoInput<OUTPUT> {
    abstract fun execute(): OUTPUT
}
