package io.github.janbarari.gradle.analytics.domain.usecase

import io.github.janbarari.gradle.analytics.core.UseCase
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository

class GetMetricsUseCase(
    private val repo: DatabaseRepository
): UseCase<Long, List<BuildMetric>>() {

    override fun execute(input: Long): List<BuildMetric> {
        return repo.getMetrics(input)
    }

}
