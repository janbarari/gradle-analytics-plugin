package io.github.janbarari.gradle.analytics.metric.modulesmethodcount.update

import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesMethodCountMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.ensureNotNull

class UpdateModulesMethodCountMetricUseCase(
    private val repo: DatabaseRepository
) : UseCaseNoInput<ModulesMethodCountMetric>() {

    override suspend fun execute(): ModulesMethodCountMetric {
        return ensureNotNull(repo.getTemporaryMetrics().last().modulesMethodCountMetric)
    }

}
