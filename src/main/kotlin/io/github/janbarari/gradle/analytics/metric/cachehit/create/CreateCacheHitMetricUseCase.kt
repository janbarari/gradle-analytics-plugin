package io.github.janbarari.gradle.analytics.metric.cachehit.create

import io.github.janbarari.gradle.analytics.domain.model.CacheHitMetric
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.core.UseCase

class CreateCacheHitMetricUseCase: UseCase<Collection<TaskInfo>, CacheHitMetric>() {

    override suspend fun execute(input: Collection<TaskInfo>): CacheHitMetric {
        return CacheHitMetric(0f, emptyList())
    }

}