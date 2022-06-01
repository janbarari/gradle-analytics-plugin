package io.github.janbarari.gradle.analytics.core

import io.github.janbarari.gradle.extension.ExcludeJacocoGenerated

@SuppressWarnings("UnnecessaryAbstractClass")
@ExcludeJacocoGenerated
abstract class UseCaseNoInput<OUTPUT> {
    abstract fun execute(): OUTPUT
}
