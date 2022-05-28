package io.github.janbarari.gradle.analytics.core

interface Stage<I, O> {
    fun process(input: I): O
}
