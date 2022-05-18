package io.github.janbarari.gradle.analytics.domain.usecase

import io.github.janbarari.gradle.analytics.core.usecase.UseCaseNoInput
import io.github.janbarari.gradle.analytics.domain.metric.InitializationMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.utils.longMedian

class InitializationMetricMedianUseCase(
    private val repo: DatabaseRepository
) : UseCaseNoInput<InitializationMetric>() {

    override fun execute(): InitializationMetric {
        val durations = arrayListOf<Long>()
        val temporaryMetrics = repo.getTemporaryMetrics()

        val iterator = temporaryMetrics.iterator()
        while (iterator.hasNext()) {
            val temp = iterator.next()
            temp.initializationMetric?.average?.let {
                durations.add(it)
            }
        }

        return InitializationMetric(
            average = longMedian(durations)
        )
    }

}
