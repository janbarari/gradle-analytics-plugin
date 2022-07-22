package io.github.janbarari.gradle.analytics.metric.modulesourcecount.update

import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesSourceCountMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.ensureNotNull

class UpdateModulesSourceCountMetricUseCase(
    private val repo: DatabaseRepository
) : UseCaseNoInput<ModulesSourceCountMetric>() {

    override suspend fun execute(): ModulesSourceCountMetric {
        return ensureNotNull(repo.getTemporaryMetrics().last().modulesSourceCountMetric)
    }

}
