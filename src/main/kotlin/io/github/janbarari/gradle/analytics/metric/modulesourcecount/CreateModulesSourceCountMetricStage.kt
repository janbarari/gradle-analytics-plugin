package io.github.janbarari.gradle.analytics.metric.modulesourcecount

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ModuleInfo
import io.github.janbarari.gradle.core.Stage

class CreateModulesSourceCountMetricStage(
    private val modulesInfo: List<ModuleInfo>,
    private val createModulesSourceCountMetricUseCase: CreateModulesSourceCountMetricUseCase
): Stage<BuildMetric, BuildMetric> {

    override suspend fun process(input: BuildMetric): BuildMetric {
        input.modulesSourceCountMetric = createModulesSourceCountMetricUseCase.execute(
            modulesInfo
        )
        return input
    }

}