package io.github.janbarari.gradle.analytics.metric.modulesourcecount

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ModulesSourceCountMetric
import io.github.janbarari.gradle.analytics.domain.model.ModulesSourceCountReport
import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.hasMultipleItems
import io.github.janbarari.gradle.extension.hasSingleItem
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.whenEach

@Suppress("MagicNumber")
class CreateModulesSourceCountReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    override suspend fun process(input: Report): Report {
        val metrics: List<ModulesSourceCountMetric> = metrics
            .filter {
                it.modulesSourceCountMetric.isNotNull()
            }
            .map {
                ensureNotNull(it.modulesSourceCountMetric)
            }

        var result: ModulesSourceCountReport? = null

        if (metrics.isEmpty()) {
            result = null
        }

        if (metrics.hasSingleItem()) {
            result = generateSingleItemReport(ensureNotNull(metrics.single()))
        }

        if (metrics.hasMultipleItems()) {
            result = generateMultipleItemsReport(metrics)
        }

        return input.apply {
            modulesSourceCountReport = result
        }
    }

    private fun generateSingleItemReport(metric: ModulesSourceCountMetric): ModulesSourceCountReport {
        var totalSourceCount = 0
        metric.modules.whenEach {
            totalSourceCount += value
        }
        return ModulesSourceCountReport(
            values = metric.modules,
            totalSourceCount = totalSourceCount,
            totalDiffRatio = null // The ratio does not exist when there is only one item
        )
    }

    private fun generateMultipleItemsReport(metrics: List<ModulesSourceCountMetric>): ModulesSourceCountReport? {
        return null
    }

}
