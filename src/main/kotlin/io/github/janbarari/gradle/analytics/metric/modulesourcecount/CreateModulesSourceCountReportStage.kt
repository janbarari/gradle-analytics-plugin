package io.github.janbarari.gradle.analytics.metric.modulesourcecount

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ModuleSourceCount
import io.github.janbarari.gradle.analytics.domain.model.ModulesSourceCountReport
import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.firstIndex
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull

class CreateModulesSourceCountReportStage(
    private val metrics: List<BuildMetric>
): Stage<Report, Report> {

    @Suppress("MagicNumber")
    override suspend fun process(input: Report): Report {
        val values = mutableListOf<ModuleSourceCount>()

        val firstMetricTotalSourceCount = getMetricTotalSourceCount(metrics.firstIndex)
        val lastMetricTotalSourceCount = getMetricTotalSourceCount(metrics.lastIndex)

        val totalDiffRatio = 0f
        if (firstMetricTotalSourceCount > 0) {
            ((lastMetricTotalSourceCount - firstMetricTotalSourceCount) / firstMetricTotalSourceCount) * 100F
        }

        metrics.last().modulesSourceCountMetric.whenNotNull {
            modules.whenEach {
                values.add(
                    ModuleSourceCount(
                        path = this.path,
                        value = this.value,
                        coverage = (this.value / lastMetricTotalSourceCount) * 100f,
                        diffRatio = (this.value / getMetricModuleSourceCount(path, metrics.firstIndex)) * 100f
                    )
                )
            }
        }

        val report = ModulesSourceCountReport(
            values = values,
            totalSourceCount = lastMetricTotalSourceCount,
            totalDiffRatio = totalDiffRatio
        )
        return input.apply {
            modulesSourceCountReport = report
        }
    }

    private fun getMetricTotalSourceCount(index: Int): Int {
        var result = 0
        metrics[index].modulesSourceCountMetric.whenNotNull {
            result = this.modules.sumOf { it.value }
        }
        return result
    }

    private fun getMetricModuleSourceCount(path: String, index: Int): Int {
        var result = 0
        metrics[index].modulesSourceCountMetric.whenNotNull {
            modules.whenEach {
                if (this.path == path) {
                    result = this.value
                }
            }
        }
        return result
    }

}
