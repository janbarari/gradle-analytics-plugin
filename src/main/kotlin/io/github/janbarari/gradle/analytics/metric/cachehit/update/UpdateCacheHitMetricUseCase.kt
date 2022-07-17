package io.github.janbarari.gradle.analytics.metric.cachehit.update

import io.github.janbarari.gradle.analytics.domain.model.CacheHitMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.ensureNotNull

class UpdateCacheHitMetricUseCase(
    private val repo: DatabaseRepository
): UseCaseNoInput<CacheHitMetric>() {

    override suspend fun execute(): CacheHitMetric {
        return ensureNotNull(
            repo.getTemporaryMetrics().last().cacheHitMetric
        )
    }

}
