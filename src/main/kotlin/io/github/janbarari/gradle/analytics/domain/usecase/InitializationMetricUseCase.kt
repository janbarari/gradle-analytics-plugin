package io.github.janbarari.gradle.analytics.domain.usecase

import io.github.janbarari.gradle.analytics.core.usecase.UseCase
import io.github.janbarari.gradle.analytics.domain.metric.InitializationMetric

class InitializationMetricUseCase: UseCase<Long, InitializationMetric>() {
    override fun execute(input: Long): InitializationMetric {
        return InitializationMetric(input)
    }
}