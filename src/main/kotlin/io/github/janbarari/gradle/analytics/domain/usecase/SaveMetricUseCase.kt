package io.github.janbarari.gradle.analytics.domain.usecase

import io.github.janbarari.gradle.analytics.domain.core.UseCase
import io.github.janbarari.gradle.analytics.domain.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository

class SaveMetricUseCase(
    private val repo: DatabaseRepository,
    private val initializationMetricMedianUseCase: InitializationMetricMedianUseCase
): UseCase<BuildMetric, Boolean>() {

    override fun execute(new: BuildMetric): Boolean {
        if (repo.isDayMetricExists()) {
            val tempMetric = BuildMetric()

            val dayMetric = repo.getDayMetric()
            val dayMetricNumber = dayMetric.second

            tempMetric.initializationMetric = initializationMetricMedianUseCase.execute()

            return repo.updateDayMetric(dayMetricNumber, tempMetric)
        }

        return repo.saveNewMetric(new)
    }

}
