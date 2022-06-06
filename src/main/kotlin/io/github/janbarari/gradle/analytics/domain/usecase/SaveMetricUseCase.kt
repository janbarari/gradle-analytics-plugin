package io.github.janbarari.gradle.analytics.domain.usecase

import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.metric.configuration.UpdateConfigurationMetricStage
import io.github.janbarari.gradle.analytics.metric.configuration.UpdateConfigurationMetricUseCase
import io.github.janbarari.gradle.analytics.metric.execution.UpdateExecutionMetricStage
import io.github.janbarari.gradle.analytics.metric.execution.UpdateExecutionMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.UpdateInitializationMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.UpdateInitializationMetricStage

class SaveMetricUseCase(
    private val repo: DatabaseRepository,
    private val updateInitializationMetricUseCase: UpdateInitializationMetricUseCase,
    private val updateConfigurationMetricUseCase: UpdateConfigurationMetricUseCase,
    private val updateExecutionMetricUseCase: UpdateExecutionMetricUseCase
): UseCase<BuildMetric, Long>() {

    override fun execute(input: BuildMetric): Long {

        if (repo.isDayMetricExists()) {

            val updateInitializationMetricStage = UpdateInitializationMetricStage(updateInitializationMetricUseCase)
            val updateConfigurationMetricStage = UpdateConfigurationMetricStage(updateConfigurationMetricUseCase)
            val updateExecutionMetricStage = UpdateExecutionMetricStage(updateExecutionMetricUseCase)

            val updatedMetric = UpdateMetricPipeline(updateInitializationMetricStage)
                .addStage(updateConfigurationMetricStage)
                .addStage(updateExecutionMetricStage)
                .execute(BuildMetric(input.branch, input.requestedTasks, input.createdAt))

            val dayMetricNumber = repo.getDayMetric().second

            repo.updateDayMetric(dayMetricNumber, updatedMetric)
            return dayMetricNumber
        }

        return repo.saveNewMetric(input)
    }

}
