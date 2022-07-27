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
package io.github.janbarari.gradle.analytics.metric.cachehit.report

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.report.CacheHitReport
import io.github.janbarari.gradle.analytics.domain.model.ChartPoint
import io.github.janbarari.gradle.analytics.domain.model.report.ModuleCacheHitReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.analytics.domain.model.TimespanChartPoint
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.diffPercentageOf
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.hasMultipleItems
import io.github.janbarari.gradle.extension.hasSingleItem
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.utils.DatasetUtils

class CreateCacheHitReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    override suspend fun process(report: Report): Report {
        val metrics = metrics.filter {
            it.cacheHitMetric.isNotNull()
        }

        if (metrics.hasSingleItem()) {
            return report.apply {
                cacheHitReport = generateSingleItemReport(metrics.single())
            }
        }

        if (metrics.hasMultipleItems()) {
            return report.apply {
                cacheHitReport = generateMultipleItemsReport(metrics)
            }
        }

        return report
    }

    private fun generateSingleItemReport(metric: BuildMetric): CacheHitReport {
        val modules = mutableListOf<ModuleCacheHitReport>()
        val overallHit = ensureNotNull(metric.cacheHitMetric).hitRatio

        val overallHitTimespanChartPoint = TimespanChartPoint(
            value = overallHit,
            from = metric.createdAt
        )

        val overallValues = listOf(
            ChartPoint(
                value = overallHitTimespanChartPoint.value,
                description = overallHitTimespanChartPoint.getTimespanString()
            )
        )

        ensureNotNull(metric.cacheHitMetric).modules.whenEach {
            val values = mutableListOf<ChartPoint>()
            TimespanChartPoint(
                value = hitRatio, from = metric.createdAt
            ).also {
                values.add(
                    ChartPoint(
                        value = it.value, description = it.getTimespanString()
                    )
                )
            }
            modules.add(
                ModuleCacheHitReport(
                    path = path,
                    hitRatio = hitRatio,
                    diffRatio = null,
                    values = values
                )
            )
        }

        return CacheHitReport(
            modules = modules.sortedByDescending { it.hitRatio },
            overallHit = overallHit,
            overallDiffRatio = null,
            overallValues = overallValues
        )
    }

    private fun generateMultipleItemsReport(metrics: List<BuildMetric>): CacheHitReport {
        val firstCacheHitRatio = ensureNotNull(metrics.first().cacheHitMetric).hitRatio
        val lastCacheHitRatio = ensureNotNull(metrics.last().cacheHitMetric).hitRatio

        val overallDiffRatio = firstCacheHitRatio.diffPercentageOf(lastCacheHitRatio)

        val overallHit = ensureNotNull(metrics.last().cacheHitMetric).hitRatio

        val overallValuesTimestampChartPoints = mutableListOf<TimespanChartPoint>()
        metrics.whenEach {
            overallValuesTimestampChartPoints.add(
                TimespanChartPoint(
                    value = ensureNotNull(cacheHitMetric).hitRatio, from = createdAt
                )
            )
        }
        val minimizedOverallValues =
            DatasetUtils.minimizeTimespanChartPoints(overallValuesTimestampChartPoints, 12)
        val overallValues = minimizedOverallValues.map {
            ChartPoint(it.value, it.getTimespanString())
        }

        val modules = mutableListOf<ModuleCacheHitReport>()
        ensureNotNull(metrics.last().cacheHitMetric).modules.whenEach {
                modules.add(
                    ModuleCacheHitReport(
                        path = path,
                        hitRatio = hitRatio,
                        diffRatio = calculateModuleCacheHitDiffRatio(metrics, path, hitRatio),
                        values = getModuleChartPoints(path)
                    )
                )
            }

        return CacheHitReport(
            modules = modules,
            overallValues = overallValues,
            overallHit = overallHit,
            overallDiffRatio = overallDiffRatio
        )
    }

    private fun calculateModuleCacheHitDiffRatio(metrics: List<BuildMetric>, path: String, value: Long): Float? {
        return ensureNotNull(metrics.first().cacheHitMetric)
            .modules.find { it.path == path }?.hitRatio?.diffPercentageOf(value)
    }

    private fun getModuleChartPoints(path: String): List<ChartPoint> {
        val timestampChartPoints = mutableListOf<TimespanChartPoint>()
        metrics
            .filter {
                it.cacheHitMetric.isNotNull()
            }.whenEach {
                ensureNotNull(cacheHitMetric).modules.filter { it.path == path }.whenEach {
                        timestampChartPoints.add(
                            TimespanChartPoint(
                                value = hitRatio,
                                from = createdAt
                            )
                        )
                    }
            }
        val minimizedOverallValues = DatasetUtils.minimizeTimespanChartPoints(timestampChartPoints, 12)
        return minimizedOverallValues.map {
            ChartPoint(it.value, it.getTimespanString())
        }
    }
}
