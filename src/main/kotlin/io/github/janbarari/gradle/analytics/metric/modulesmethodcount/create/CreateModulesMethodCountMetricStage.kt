package io.github.janbarari.gradle.analytics.metric.modulesmethodcount.create

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.core.Stage

class CreateModulesMethodCountMetricStage(
    private val modulesInfo: List<ModulePath>,
    private val createModulesMethodCountMetricUseCase: CreateModulesMethodCountMetricUseCase
): Stage<BuildMetric, BuildMetric> {

    override suspend fun process(input: BuildMetric): BuildMetric {
        input.modulesMethodCountMetric = createModulesMethodCountMetricUseCase.execute(
            modulesInfo
        )
        return input
    }

}
