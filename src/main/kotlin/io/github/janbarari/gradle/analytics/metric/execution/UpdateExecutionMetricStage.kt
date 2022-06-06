package io.github.janbarari.gradle.analytics.metric.execution

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.core.Stage

class UpdateExecutionMetricStage(
    private val updateExecutionMetricUseCase: UpdateExecutionMetricUseCase
): Stage<BuildMetric, BuildMetric> {

    override fun process(input: BuildMetric): BuildMetric {
        input.executionMetric = updateExecutionMetricUseCase.execute()
        return input
    }

}
