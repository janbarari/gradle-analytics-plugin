package io.github.janbarari.gradle.analytics.metric.initialization.stage

import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.metric.initialization.usecase.CreateInitializationMetricUseCase
import io.github.janbarari.gradle.core.Stage

class CreateInitializationMetricStage(
    private val info: BuildInfo,
    private val createInitializationMetricUseCase: CreateInitializationMetricUseCase
): Stage<BuildMetric, BuildMetric> {
    override fun process(input: BuildMetric): BuildMetric {
        input.initializationMetric = createInitializationMetricUseCase.execute(
            info.getInitializationDuration().toMillis()
        )
        return input
    }
}
