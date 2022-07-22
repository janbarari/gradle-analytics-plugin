package io.github.janbarari.gradle.analytics.metric.modulesmethodcount.update

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.core.Stage

class UpdateModulesMethodCountMetricStage(
    private val updateModulesMethodCountMetricUseCase: UpdateModulesMethodCountMetricUseCase
): Stage<BuildMetric, BuildMetric> {

    override suspend fun process(input: BuildMetric): BuildMetric {
        input.modulesMethodCountMetric = updateModulesMethodCountMetricUseCase.execute()
        return input
    }

}
