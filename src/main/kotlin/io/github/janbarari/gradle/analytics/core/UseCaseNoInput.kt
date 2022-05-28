package io.github.janbarari.gradle.analytics.core

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class UseCaseNoInput<OUTPUT> {
    abstract fun execute(): OUTPUT
}
