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
import io.github.janbarari.gradle.analytics.domain.model.report.ModuleCacheHit
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.analytics.domain.model.TimespanPoint
import io.github.janbarari.gradle.core.SuspendStage
import io.github.janbarari.gradle.extension.diffPercentageOf
import io.github.janbarari.gradle.extension.hasMultipleItems
import io.github.janbarari.gradle.extension.hasSingleItem
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.minimize
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.logger.Tower

class CreateCacheHitReportStage(
    private val tower: Tower,
    private val metrics: List<BuildMetric>
) : SuspendStage<Report, Report> {

    companion object {
        private val clazz = CreateCacheHitReportStage::class.java
    }

    companion object {
        private val clazz = CreateCacheHitReportStage::class.java
    }

    override suspend fun process(input: Report): Report {
        tower.i(clazz, "process()")
        val metrics = metrics.filter {
            it.cacheHitMetric.isNotNull()
        }

        if (metrics.hasSingleItem()) {
            return input.apply {
                cacheHitReport = generateSingleItemReport(metrics.single())
            }
        }

        if (metrics.hasMultipleItems()) {
            return input.apply {
                cacheHitReport = generateMultipleItemsReport(metrics)
            }
        }

        return input
    }

    private fun generateSingleItemReport(metric: BuildMetric): CacheHitReport {
        val modules = mutableListOf<ModuleCacheHit>()
        val overallHit = metric.cacheHitMetric!!.rate

        val overallHitTimespanChartPoint = TimespanPoint(
            value = overallHit,
            from = metric.createdAt
        )

        val overallValues = listOf(
            overallHitTimespanChartPoint
        )

        metric.cacheHitMetric!!.modules.whenEach {
            val values = mutableListOf(
                TimespanPoint(
                    value = rate, from = metric.createdAt
                )
            )
            modules.add(
                ModuleCacheHit(
                    path = path,
                    rate = rate,
                    diffRate = null,
                    meanValues = values
                )
            )
        }

        return CacheHitReport(
            modules = modules.sortedByDescending { it.rate },
            overallRate = overallHit,
            overallDiffRate = null,
            overallMeanValues = overallValues
        )
    }

    private fun generateMultipleItemsReport(metrics: List<BuildMetric>): CacheHitReport {
        val firstCacheHitRatio = metrics.first().cacheHitMetric!!.rate
        val lastCacheHitRatio = metrics.last().cacheHitMetric!!.rate

        val overallDiffRatio = firstCacheHitRatio.diffPercentageOf(lastCacheHitRatio)

        val overallHit = metrics.last().cacheHitMetric!!.rate

        val overallValuesTimestampChartPoints = mutableListOf<TimespanPoint>()
        metrics.whenEach {
            overallValuesTimestampChartPoints.add(
                TimespanPoint(
                    value = cacheHitMetric!!.rate, from = createdAt
                )
            )
        }
        val overallMeanValues = overallValuesTimestampChartPoints.minimize(12)

        val modules = mutableListOf<ModuleCacheHit>()
        metrics.last().cacheHitMetric!!.modules.whenEach {
                modules.add(
                    ModuleCacheHit(
                        path = path,
                        rate = rate,
                        diffRate = calculateModuleCacheHitDiffRatio(metrics, path, rate),
                        meanValues = getModuleChartPoints(path)
                    )
                )
            }

        return CacheHitReport(
            modules = modules,
            overallMeanValues = overallMeanValues,
            overallRate = overallHit,
            overallDiffRate = overallDiffRatio
        )
    }

    private fun calculateModuleCacheHitDiffRatio(metrics: List<BuildMetric>, path: String, value: Long): Float? {
        return metrics.first().cacheHitMetric!!
            .modules.find { it.path == path }?.rate?.diffPercentageOf(value)
    }

    private fun getModuleChartPoints(path: String): List<TimespanPoint> {
        val timestampChartPoints = mutableListOf<TimespanPoint>()
        metrics
            .filter {
                it.cacheHitMetric.isNotNull()
            }.whenEach {
                cacheHitMetric!!.modules
                    .filter { it.path == path }
                    .whenEach {
                        timestampChartPoints.add(
                            TimespanPoint(
                                value = rate,
                                from = createdAt
                            )
                        )
                    }
            }
        
        return timestampChartPoints.minimize(12)
    }
}
