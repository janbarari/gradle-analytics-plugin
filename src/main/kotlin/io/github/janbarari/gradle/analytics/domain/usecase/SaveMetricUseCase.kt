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

    override fun execute(new: BuildMetric): Long {
        if (repo.isDayMetricExists()) {
            val updateInitializationMetricStage = UpdateInitializationMetricStage(updateInitializationMetricUseCase)

            val metric = UpdateMetricPipeline(updateInitializationMetricStage)
                .execute(BuildMetric(new.branch, new.requestedTasks, new.createdAt))

            val dayMetric = repo.getDayMetric()

            repo.updateDayMetric(dayMetric.second, metric)
            return dayMetric.second
        }
        return repo.saveNewMetric(new)
    }

}
