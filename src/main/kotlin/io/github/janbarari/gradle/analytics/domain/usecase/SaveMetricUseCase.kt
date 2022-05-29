package io.github.janbarari.gradle.analytics.domain.usecase

import io.github.janbarari.gradle.analytics.core.UseCase
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.metric.initialization.InitializationMetricMedianUseCase

class SaveMetricUseCase(
    private val repo: DatabaseRepository,
    private val initializationMetricMedianUseCase: InitializationMetricMedianUseCase
): UseCase<BuildMetric, Boolean>() {

    override fun execute(new: BuildMetric): Boolean {
        if (repo.isDayMetricExists()) {
            val tempMetric = BuildMetric(new.branch, new.requestedTasks, new.createdAt)

            val dayMetric = repo.getDayMetric()
            val dayMetricNumber = dayMetric.second

            tempMetric.initializationMetric = initializationMetricMedianUseCase.execute(
                Pair(new.branch, new.requestedTasks)
            )

            return repo.updateDayMetric(dayMetricNumber, tempMetric)
        }

        return repo.saveNewMetric(new)
    }

}
