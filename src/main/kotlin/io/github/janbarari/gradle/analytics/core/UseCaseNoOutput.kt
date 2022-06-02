package io.github.janbarari.gradle.analytics.core

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@SuppressWarnings("UnnecessaryAbstractClass")
@ExcludeJacocoGenerated
abstract class UseCaseNoOutput<INPUT> {
    abstract fun execute(input: INPUT)
}
