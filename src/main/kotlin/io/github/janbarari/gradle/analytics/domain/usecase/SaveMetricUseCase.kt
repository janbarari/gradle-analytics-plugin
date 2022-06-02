package io.github.janbarari.gradle.analytics.domain.usecase

import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.metric.initialization.InitializationMetricMedianUseCase
import io.github.janbarari.gradle.extension.ensureNotNull

class SaveMetricUseCase(
    private val repo: DatabaseRepository,
    private val initializationMetricMedianUseCase: InitializationMetricMedianUseCase
): UseCase<BuildMetric, Long>() {

    override fun execute(new: BuildMetric): Long {
        if (repo.isDayMetricExists()) {
            val tempMetric = BuildMetric(new.branch, new.requestedTasks, new.createdAt)

            val dayMetric = repo.getDayMetric()
            val dayMetricNumber = ensureNotNull(dayMetric).second

            tempMetric.initializationMetric = initializationMetricMedianUseCase.execute(
                Pair(new.branch, new.requestedTasks)
            )

            repo.updateDayMetric(dayMetricNumber, tempMetric)
            return dayMetricNumber
        }
        return repo.saveNewMetric(new)
    }

}
