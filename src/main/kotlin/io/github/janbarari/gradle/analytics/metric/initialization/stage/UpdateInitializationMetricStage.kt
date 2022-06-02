package io.github.janbarari.gradle.analytics.metric.initialization.stage

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.metric.initialization.usecase.UpdateInitializationMetricUseCase
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull

class UpdateInitializationMetricStage(
    private val updateInitializationMetricUseCase: UpdateInitializationMetricUseCase
): Stage<BuildMetric, BuildMetric> {

    override fun process(input: BuildMetric): BuildMetric {
        input.initializationMetric = updateInitializationMetricUseCase.execute()
        return input
    }

}