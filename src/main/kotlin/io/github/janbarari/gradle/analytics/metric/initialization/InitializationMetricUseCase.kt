package io.github.janbarari.gradle.analytics.metric.initialization

import io.github.janbarari.gradle.analytics.core.UseCase
import io.github.janbarari.gradle.analytics.domain.model.InitializationMetric

class InitializationMetricUseCase: UseCase<Long, InitializationMetric>() {
    override fun execute(input: Long): InitializationMetric {
        return InitializationMetric(input)
    }
}
