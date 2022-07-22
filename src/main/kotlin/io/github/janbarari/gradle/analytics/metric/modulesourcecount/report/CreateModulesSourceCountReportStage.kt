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
package io.github.janbarari.gradle.analytics.metric.modulesourcecount.report

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.report.ModuleSourceCountReport
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesSourceCountMetric
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesSourceCountReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
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
