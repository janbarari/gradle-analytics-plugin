package io.github.janbarari.gradle.analytics.metric.configuration

import io.github.janbarari.gradle.analytics.domain.model.ConfigurationMetric
import io.github.janbarari.gradle.core.UseCase

class CreateConfigurationMetricUseCase: UseCase<Long, ConfigurationMetric>() {
    override fun execute(input: Long): ConfigurationMetric {
        return ConfigurationMetric(input)
    }
}
