package io.github.janbarari.gradle.analytics.metric.configuration

import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.core.Stage

class CreateConfigurationMetricStage(
    private val info: BuildInfo,
    private val createConfigurationMetricUseCase: CreateConfigurationMetricUseCase
): Stage<BuildMetric, BuildMetric> {
    override fun process(input: BuildMetric): BuildMetric {
        input.configurationMetric = createConfigurationMetricUseCase.execute(
            info.getConfigurationDuration().toMillis()
        )
        return input
    }
}
