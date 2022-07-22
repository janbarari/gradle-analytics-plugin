package io.github.janbarari.gradle.analytics.metric.modulesourcecount.report

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ModuleSourceCountReport
import io.github.janbarari.gradle.analytics.domain.model.ModulesSourceCountMetric
import io.github.janbarari.gradle.analytics.domain.model.ModulesSourceCountReport
import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.hasMultipleItems
import io.github.janbarari.gradle.extension.hasSingleItem
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.diffPercentageOf
import io.github.janbarari.gradle.extension.toPercentageOf
import io.github.janbarari.gradle.extension.whenEach

class CreateModulesSourceCountReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    override suspend fun process(input: Report): Report {
        val metrics: List<ModulesSourceCountMetric> = metrics.filter {
                it.modulesSourceCountMetric.isNotNull()
            }.map {
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
        val values = mutableListOf<ModuleSourceCountReport>()

        metric.modules.whenEach {
            totalSourceCount += value
        }

        metric.modules.whenEach {
            values.add(
                ModuleSourceCountReport(
                    path = path,
                    value = value,
                    coverage = value.toPercentageOf(totalSourceCount),
                    diffRatio = null
                )
            )
        }

        return ModulesSourceCountReport(
            values = values.sortedByDescending { it.value },
            totalSourceCount = totalSourceCount,
            totalDiffRatio = null // The ratio does not exist when there is only one item
        )
    }

    private fun generateMultipleItemsReport(metrics: List<ModulesSourceCountMetric>): ModulesSourceCountReport? {

        var firstTotalSourceCount = 0
        metrics.first().modules.whenEach {
            firstTotalSourceCount += value
        }

        var lastTotalSourceCount = 0
        metrics.last().modules.whenEach {
            lastTotalSourceCount += value
        }

        val totalDiffRatio = firstTotalSourceCount.diffPercentageOf(lastTotalSourceCount)

        val values = mutableListOf<ModuleSourceCountReport>()
        metrics.last().modules.whenEach {
            values.add(
                ModuleSourceCountReport(
                    path = path,
                    value = value,
                    coverage = value.toPercentageOf(lastTotalSourceCount),
                    diffRatio = calculateModuleDiffRatio(metrics, path, value)
                )
            )
        }

        return ModulesSourceCountReport(
            values = values.sortedByDescending { it.value },
            totalSourceCount = lastTotalSourceCount,
            totalDiffRatio = totalDiffRatio
        )
    }

    private fun calculateModuleDiffRatio(metrics: List<ModulesSourceCountMetric>, path: String, value: Int): Float? {
        return metrics.first().modules.find { it.path == path }?.value?.diffPercentageOf(value)
    }

}
