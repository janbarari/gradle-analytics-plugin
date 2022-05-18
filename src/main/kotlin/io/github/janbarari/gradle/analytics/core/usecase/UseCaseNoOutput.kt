package io.github.janbarari.gradle.analytics.core.usecase

abstract class UseCaseNoOutput<INPUT> {
    abstract fun execute(input: INPUT)
}
