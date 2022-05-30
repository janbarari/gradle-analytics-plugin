package io.github.janbarari.gradle.analytics.metric.initialization

import io.github.janbarari.gradle.analytics.core.UseCase
import io.github.janbarari.gradle.analytics.domain.model.InitializationMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.utils.MathUtils

class InitializationMetricMedianUseCase(
    private val repo: DatabaseRepository
) : UseCase<Pair<String, List<String>>, InitializationMetric>() {

    override fun execute(input: Pair<String, List<String>>): InitializationMetric {
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
            average = MathUtils.longMedian(durations)
        )
    }

}
