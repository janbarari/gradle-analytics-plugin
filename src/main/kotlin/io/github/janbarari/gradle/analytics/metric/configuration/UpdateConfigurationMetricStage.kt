package io.github.janbarari.gradle.analytics.metric.configuration

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.core.Stage

class UpdateConfigurationMetricStage(
    private val updateConfigurationMetricUseCase: UpdateConfigurationMetricUseCase
): Stage<BuildMetric, BuildMetric> {
    override fun process(input: BuildMetric): BuildMetric {
        input.configurationMetric = updateConfigurationMetricUseCase.execute()
        return input
    }
}
