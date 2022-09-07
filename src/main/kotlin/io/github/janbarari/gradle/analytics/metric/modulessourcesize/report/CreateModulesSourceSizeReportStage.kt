/**
 * MIT License
 * Copyright (c) 2022 Mehdi Janbarari (@janbarari)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.metric.modulessourcesize.report

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.report.ModuleMethodCount
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesMethodCountMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesSourceSizeMetric
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesMethodCountReport
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesSourceSizeReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.diffPercentageOf
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.hasMultipleItems
import io.github.janbarari.gradle.extension.hasSingleItem
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.toPercentageOf
import io.github.janbarari.gradle.extension.whenEach

class CreateModulesSourceSizeReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    override suspend fun process(report: Report): Report {
        val metrics = metrics.filter {
            it.modulesSourceSizeMetric.isNotNull()
        }.map {
            ensureNotNull(it.modulesSourceSizeMetric)
        }

        if (metrics.hasSingleItem()) {
            return report.apply {
                modulesSourceSizeReport = generateSingleItemReport(metrics.single())
            }
        }

        if (metrics.hasMultipleItems()) {
            return report.apply {
                modulesSourceSizeReport = generateMultipleItemsReport(metrics)
            }
        }

        return report
    }

    fun generateSingleItemReport(metric: ModulesSourceSizeMetric): ModulesSourceSizeReport {
        val values = mutableListOf<ModulesSourceSizeReport.ModuleSourceSize>()

        val totalSourceCount = metric.modules.sumOf { it.sizeInKb }

        metric.modules.whenEach {
            values.add(
                ModulesSourceSizeReport.ModuleSourceSize(
                    path = path,
                    sizeInKb = sizeInKb,
                    coverage = sizeInKb.toPercentageOf(totalSourceCount),
                    diffRate = null // The ratio does not exist when there is only one item
                )
            )
        }

        return ModulesSourceSizeReport(
            values = values.sortedByDescending { it.sizeInKb },
            totalSourceSizeInKb = totalSourceCount,
            totalDiffRate = null // The ratio does not exist when there is only one item
        )
    }

    fun generateMultipleItemsReport(metrics: List<ModulesSourceSizeMetric>): ModulesSourceSizeReport {
        val firstTotalSourceCount = metrics.first().modules.sumOf { it.sizeInKb }
        val lastTotalSourceCount = metrics.last().modules.sumOf { it.sizeInKb }
        val totalDiffRatio = firstTotalSourceCount.diffPercentageOf(lastTotalSourceCount)

        val values = mutableListOf<ModulesSourceSizeReport.ModuleSourceSize>()
        metrics.last().modules.whenEach {
            values.add(
                ModulesSourceSizeReport.ModuleSourceSize(
                    path = path,
                    sizeInKb = sizeInKb,
                    coverage = sizeInKb.toPercentageOf(lastTotalSourceCount),
                    diffRate = calculateModuleDiffRatio(metrics, path, sizeInKb)
                )
            )
        }

        return ModulesSourceSizeReport(
            values = values.sortedByDescending { it.sizeInKb },
            totalSourceSizeInKb = lastTotalSourceCount,
            totalDiffRate = totalDiffRatio
        )
    }

    fun calculateModuleDiffRatio(metrics: List<ModulesSourceSizeMetric>, path: String, value: Long): Float? {
        return metrics.first().modules.find { it.path == path }?.sizeInKb?.diffPercentageOf(value)
    }

}
