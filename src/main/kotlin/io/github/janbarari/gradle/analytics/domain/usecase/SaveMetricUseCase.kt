package io.github.janbarari.gradle.analytics.domain.usecase

import io.github.janbarari.gradle.analytics.core.usecase.UseCase
import io.github.janbarari.gradle.analytics.domain.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.metric.InitializationMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository

class SaveMetricUseCase(private val repo: DatabaseRepository): UseCase<BuildMetric, Boolean>() {

    override fun execute(newMetric: BuildMetric): Boolean {
        if (repo.isTodayMetricExists()) {
            val currentMetric = repo.getTodayMetric()
            val meanMetric = BuildMetric()

            var meanInitializationDuration = 0L
            val currentInitializationDuration: Long = currentMetric?.first?.initializationMetric?.average ?: 0L
            val newInitializationDuration: Long = newMetric.initializationMetric?.average ?: 0L
            if (currentInitializationDuration > 0 && newInitializationDuration > 0) {
                meanInitializationDuration = (currentInitializationDuration + newInitializationDuration)/2
            }
            if (currentInitializationDuration == 0L && newInitializationDuration > 0) {
                meanInitializationDuration = newInitializationDuration
            }
            if (newInitializationDuration == 0L && currentInitializationDuration > 0) {
                meanInitializationDuration = currentInitializationDuration
            }
            meanMetric.initializationMetric = InitializationMetric(meanInitializationDuration)

            return repo.updateExistingMetric(currentMetric?.second, meanMetric)
        }
        return repo.saveNewMetric(newMetric)
    }

}