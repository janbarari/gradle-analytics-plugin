package io.github.janbarari.gradle.analytics.metric.totalbuild

import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.core.Stage

class CreateTotalBuildMetricStage(
    private val info: BuildInfo,
    private val createTotalBuildMetricUseCase: CreateTotalBuildMetricUseCase
): Stage<BuildMetric, BuildMetric> {

    override fun process(input: BuildMetric): BuildMetric {
        input.totalBuildMetric = createTotalBuildMetricUseCase.execute(
            info.getTotalDuration().toMillis()
        )
        return input
    }

}
