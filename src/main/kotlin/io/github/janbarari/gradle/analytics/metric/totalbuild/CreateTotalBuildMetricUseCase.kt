package io.github.janbarari.gradle.analytics.metric.totalbuild

import io.github.janbarari.gradle.analytics.domain.model.TotalBuildMetric
import io.github.janbarari.gradle.core.UseCase

class CreateTotalBuildMetricUseCase: UseCase<Long, TotalBuildMetric>() {

    override fun execute(input: Long): TotalBuildMetric {
        return TotalBuildMetric(input)
    }

}
