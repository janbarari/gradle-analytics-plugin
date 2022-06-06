package io.github.janbarari.gradle.analytics.metric.execution

import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.core.Stage

class CreateExecutionMetricStage(
    private val info: BuildInfo,
    private val createExecutionMetricUseCase: CreateExecutionMetricUseCase
): Stage<BuildMetric, BuildMetric> {

    override fun process(input: BuildMetric): BuildMetric {
        input.executionMetric = createExecutionMetricUseCase.execute(
            info.getExecutionDuration().toMillis()
        )
        return input
    }

}
