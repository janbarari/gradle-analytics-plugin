package io.github.janbarari.gradle.analytics.metric.modulesmethodcount.report

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ModuleMethodCountReport
import io.github.janbarari.gradle.analytics.domain.model.ModulesMethodCountMetric
import io.github.janbarari.gradle.analytics.domain.model.ModulesMethodCountReport
import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.diffPercentageOf
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.hasMultipleItems
import io.github.janbarari.gradle.extension.hasSingleItem
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.round
import io.github.janbarari.gradle.extension.toPercentageOf
import io.github.janbarari.gradle.extension.whenEach

@Suppress("MagicNumber")
class CreateModulesMethodCountReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    override suspend fun process(input: Report): Report {
        val metrics: List<ModulesMethodCountMetric> = metrics.filter {
            it.modulesMethodCountMetric.isNotNull()
        }.map {
            ensureNotNull(it.modulesMethodCountMetric)
        }

        var result: ModulesMethodCountReport? = null

        if (metrics.isEmpty()) {
            result = null
        }

        if (metrics.hasSingleItem()) {
            result = generateSingleItemReport(metrics.single())
        }

        if (metrics.hasMultipleItems()) {
            result = generateMultipleItemsReport(metrics)
        }

        return input.apply {
            modulesMethodCountReport = result
        }
    }

    private fun generateSingleItemReport(metric: ModulesMethodCountMetric): ModulesMethodCountReport {
        var totalSourceCount = 0
        val values = mutableListOf<ModuleMethodCountReport>()

        metric.modules.whenEach {
            totalSourceCount += value
        }

        metric.modules.whenEach {
            values.add(
                ModuleMethodCountReport(
                    path = path,
                    value = value,
                    coverage = value.toPercentageOf(totalSourceCount),
                    diffRatio = null
                )
            )
        }

        return ModulesMethodCountReport(
            values = values.sortedByDescending { it.value },
            totalMethodCount = totalSourceCount,
            totalDiffRatio = null // The ratio does not exist when there is only one item
        )
    }

    private fun generateMultipleItemsReport(metrics: List<ModulesMethodCountMetric>): ModulesMethodCountReport? {

        var firstTotalSourceCount = 0
        metrics.first().modules.whenEach {
            firstTotalSourceCount += value
        }

        var lastTotalSourceCount = 0
        metrics.last().modules.whenEach {
            lastTotalSourceCount += value
        }

        val totalDiffRatio = firstTotalSourceCount.diffPercentageOf(lastTotalSourceCount)

        val values = mutableListOf<ModuleMethodCountReport>()
        metrics.last().modules.whenEach {
            values.add(
                ModuleMethodCountReport(
                    path = path,
                    value = value,
                    coverage = value.toPercentageOf(lastTotalSourceCount),
                    diffRatio = calculateModuleDiffRatio(metrics, path, value)
                )
            )
        }

        return ModulesMethodCountReport(
            values = values.sortedByDescending { it.value },
            totalMethodCount = lastTotalSourceCount,
            totalDiffRatio = totalDiffRatio
        )
    }

    private fun calculateModuleDiffRatio(metrics: List<ModulesMethodCountMetric>, path: String, value: Int): Float? {
        return metrics.first().modules.find { it.path == path }?.value?.diffPercentageOf(value)
    }

}
