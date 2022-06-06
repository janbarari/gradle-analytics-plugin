package io.github.janbarari.gradle.analytics.metric.execution

import io.github.janbarari.gradle.analytics.domain.model.ExecutionMetric
import io.github.janbarari.gradle.core.UseCase

class CreateExecutionMetricUseCase: UseCase<Long, ExecutionMetric>() {

    override fun execute(input: Long): ExecutionMetric {
        return ExecutionMetric(input)
    }

}
