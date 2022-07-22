package io.github.janbarari.gradle.analytics.metric.modulesourcecount.update

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.core.Stage

class UpdateModulesSourceCountMetricStage(
    private val updateModulesSourceCountMetricUseCase: UpdateModulesSourceCountMetricUseCase
): Stage<BuildMetric, BuildMetric> {

    override suspend fun process(input: BuildMetric): BuildMetric {
        input.modulesSourceCountMetric = updateModulesSourceCountMetricUseCase.execute()
        return input
    }

}
