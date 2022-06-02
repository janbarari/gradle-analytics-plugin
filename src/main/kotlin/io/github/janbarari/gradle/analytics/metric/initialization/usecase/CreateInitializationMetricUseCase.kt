package io.github.janbarari.gradle.analytics.metric.initialization.usecase

import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.analytics.domain.model.InitializationMetric

class CreateInitializationMetricUseCase: UseCase<Long, InitializationMetric>() {
    override fun execute(input: Long): InitializationMetric {
        return InitializationMetric(input)
    }
}
