package io.github.janbarari.gradle.analytics.metric.cachehit.create

import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.core.Stage

class CreateCacheHitMetricStage(
    private val buildInfo: BuildInfo,
    private val modulesPath: List<ModulePath>,
    private val createCacheHitMetricUseCase: CreateCacheHitMetricUseCase
): Stage<BuildMetric, BuildMetric> {

    override suspend fun process(input: BuildMetric): BuildMetric {
        return input.apply {
            cacheHitMetric = createCacheHitMetricUseCase.execute(
                Pair(modulesPath ,buildInfo.executedTasks)
            )
        }
    }

}
