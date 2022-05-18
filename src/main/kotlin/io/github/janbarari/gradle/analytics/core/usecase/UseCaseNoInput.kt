package io.github.janbarari.gradle.analytics.core.usecase

abstract class UseCaseNoInput<OUTPUT> {
    abstract fun execute(): OUTPUT
}
