package io.github.janbarari.gradle.analytics.metric.totalbuild

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.core.Stage

class UpdateTotalBuildMetricStage(
    private val updateTotalBuildMetricUseCase: UpdateTotalBuildMetricUseCase
): Stage<BuildMetric, BuildMetric> {

    override fun process(input: BuildMetric): BuildMetric {
        input.totalBuildMetric = updateTotalBuildMetricUseCase.execute()
        return input
    }

}
