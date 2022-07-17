package io.github.janbarari.gradle.analytics.metric.cachehit.update

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.core.Stage

class UpdateCacheHitMetricStage(
    private val updateCacheHitMetricUseCase: UpdateCacheHitMetricUseCase
): Stage<BuildMetric, BuildMetric> {

    override suspend fun process(input: BuildMetric): BuildMetric {
        input.cacheHitMetric = updateCacheHitMetricUseCase.execute()
        return input
    }

}
