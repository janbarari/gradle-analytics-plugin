package io.github.janbarari.gradle.analytics.domain.usecase

import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.metric.initialization.usecase.UpdateInitializationMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.stage.UpdateInitializationMetricStage

class SaveMetricUseCase(
    private val repo: DatabaseRepository,
    private val updateInitializationMetricUseCase: UpdateInitializationMetricUseCase
): UseCase<BuildMetric, Long>() {

    override fun execute(input: BuildMetric): Long {

        if (repo.isDayMetricExists()) {

            val updateInitializationMetricStage = UpdateInitializationMetricStage(updateInitializationMetricUseCase)

            val updatedMetric = UpdateMetricPipeline(updateInitializationMetricStage)
                .execute(BuildMetric(input.branch, input.requestedTasks, input.createdAt))

            val dayMetricNumber = repo.getDayMetric().second

            repo.updateDayMetric(dayMetricNumber, updatedMetric)
            return dayMetricNumber
        }

        return repo.saveNewMetric(input)
    }

}
