package io.github.janbarari.gradle.analytics.metric.cachehit.create

import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.core.Stage

class CreateCacheHitMetricStage(
    private val info: BuildInfo,
    private val createCacheHitMetricUseCase: CreateCacheHitMetricUseCase
): Stage<BuildMetric, BuildMetric> {

    override suspend fun process(input: BuildMetric): BuildMetric {
        input.cacheHitMetric = createCacheHitMetricUseCase.execute(
            info.executedTasks
        )
        return input
    }

}
