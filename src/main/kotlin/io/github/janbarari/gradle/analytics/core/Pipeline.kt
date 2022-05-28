package io.github.janbarari.gradle.analytics.core

open class Pipeline<I, O>(firstStage: Stage<I, O>) {

    val currentStage: Stage<I, O> = firstStage

    fun <K> addStage(newStage: Stage<O, K>): Pipeline<I, K> {
        return Pipeline(
            object : Stage<I, K> {
                override fun process(input: I): K {
                    return newStage.process(currentStage.process(input))
                }
            }
        )
    }

    fun execute(input: I): O {
        return currentStage.process(input)
    }

}
