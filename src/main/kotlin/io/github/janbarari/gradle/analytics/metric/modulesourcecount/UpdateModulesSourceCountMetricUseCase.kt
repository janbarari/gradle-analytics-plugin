package io.github.janbarari.gradle.analytics.metric.modulesourcecount

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ModulesSourceCountMetric
import io.github.janbarari.gradle.core.UseCase

class UpdateModulesSourceCountMetricUseCase: UseCase<BuildMetric, ModulesSourceCountMetric?>() {

    override suspend fun execute(input: BuildMetric): ModulesSourceCountMetric? {
        return input.modulesSourceCountMetric
    }

}
