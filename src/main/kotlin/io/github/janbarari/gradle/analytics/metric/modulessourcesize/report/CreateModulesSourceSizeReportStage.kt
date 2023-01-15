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
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesSourceSizeMetric
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesSourceSizeReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.SuspendStage
import io.github.janbarari.gradle.extension.diffPercentageOf
import io.github.janbarari.gradle.extension.hasMultipleItems
import io.github.janbarari.gradle.extension.hasSingleItem
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.toPercentageOf
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.logger.Tower

class CreateModulesSourceSizeReportStage(
    private val tower: Tower,
    private val metrics: List<BuildMetric>
) : SuspendStage<Report, Report> {

    companion object {
        private val clazz = CreateModulesSourceSizeReportStage::class.java
    }

    override suspend fun process(input: Report): Report {
        tower.i(clazz, "process()")
        val metrics = metrics.filter {
            it.modulesSourceSizeMetric.isNotNull()
        }.map {
            it.modulesSourceSizeMetric!!
        }

        if (metrics.hasSingleItem()) {
            return input.apply {
                modulesSourceSizeReport = generateSingleItemReport(metrics.single())
            }
        }

        if (metrics.hasMultipleItems()) {
            return input.apply {
                modulesSourceSizeReport = generateMultipleItemsReport(metrics)
            }
        }

        return input
    }

    fun generateSingleItemReport(metric: ModulesSourceSizeMetric): ModulesSourceSizeReport {
        val values = mutableListOf<ModulesSourceSizeReport.ModuleSourceSize>()

        val totalSourceCount = metric.modules.sumOf { it.sizeInKb }

        metric.modules.whenEach {
            values.add(
                ModulesSourceSizeReport.ModuleSourceSize(
                    path = path,
                    sizeInKb = sizeInKb,
                    coverageRate = sizeInKb.toPercentageOf(totalSourceCount),
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
        val firstTotalSourceSize = metrics.first().modules.sumOf { it.sizeInKb }
        val lastTotalSourceSize = metrics.last().modules.sumOf { it.sizeInKb }
        val totalDiffRate = firstTotalSourceSize.diffPercentageOf(lastTotalSourceSize)

        val values = mutableListOf<ModulesSourceSizeReport.ModuleSourceSize>()
        metrics.last().modules.whenEach {
            values.add(
                ModulesSourceSizeReport.ModuleSourceSize(
                    path = path,
                    sizeInKb = sizeInKb,
                    coverageRate = sizeInKb.toPercentageOf(lastTotalSourceSize),
                    diffRate = calculateModuleDiffRate(metrics, path, sizeInKb)
                )
            )
        }

        return ModulesSourceSizeReport(
            values = values.sortedByDescending { it.sizeInKb },
            totalSourceSizeInKb = lastTotalSourceSize,
            totalDiffRate = totalDiffRate
        )
    }

    fun calculateModuleDiffRate(metrics: List<ModulesSourceSizeMetric>, path: String, value: Long): Float? {
        return metrics.first().modules.find { it.path == path }?.sizeInKb?.diffPercentageOf(value)
    }
}
