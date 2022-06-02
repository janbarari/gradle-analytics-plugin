package io.github.janbarari.gradle.analytics.domain.usecase

import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository

class SaveTemporaryMetricUseCase(private val repo: DatabaseRepository): UseCase<BuildMetric, Long>() {
    override fun execute(input: BuildMetric): Long {
        return repo.saveTemporaryMetric(input)
    }
}
