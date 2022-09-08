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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.report

import io.github.janbarari.gradle.analytics.domain.model.Module
import io.github.janbarari.gradle.analytics.domain.model.TimespanChartPoint
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.report.ModuleExecutionProcess
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesExecutionProcessReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.diffPercentageOf
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.mapToChartPoints
import io.github.janbarari.gradle.extension.minimize
import io.github.janbarari.gradle.extension.round
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.MathUtils

class CreateModulesExecutionProcessReportStage(
    private val modules: List<Module>,
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    companion object {
        private const val CHART_MAX_COLUMNS = 12
    }

    @Suppress("LongMethod")
    override suspend fun process(report: Report): Report {

        val modules = mutableListOf<ModuleExecutionProcess>()

        this.modules.forEach { module ->

            var firstAvgMedianDuration: Long? = null
            metrics.firstOrNull { metric ->
                metric.modulesExecutionProcessMetric.isNotNull()
            }.whenNotNull {
                modulesExecutionProcessMetric!!
                    .modules
                    .find { it.path == module.path }
                    .whenNotNull {
                        firstAvgMedianDuration = duration
                    }
            }

            var lastAvgMedianDuration: Long? = null
            metrics.lastOrNull { metric ->
                metric.modulesExecutionProcessMetric.isNotNull()
            }.whenNotNull {
                modulesExecutionProcessMetric!!
                    .modules
                    .find { it.path == module.path }
                    .whenNotNull {
                        lastAvgMedianDuration = duration
                    }
            }

            val avgMedianDurations = mutableListOf<TimespanChartPoint>()
            val avgMedianDuration = mutableListOf<Long>()
            val avgMedianParallelDuration = mutableListOf<Long>()
            val avgMedianParallelRate = mutableListOf<Float>()
            val avgMedianCoverage = mutableListOf<Float>()
            var diffRate: Float? = null

            if (firstAvgMedianDuration.isNotNull() && lastAvgMedianDuration.isNotNull()) {
                diffRate = firstAvgMedianDuration!!.diffPercentageOf(lastAvgMedianDuration!!)
            }

            metrics.filter { metric ->
                metric.modulesExecutionProcessMetric.isNotNull()
            }.forEach { metric ->
                metric.modulesExecutionProcessMetric!!
                    .modules
                    .find { it.path == module.path }
                    .whenNotNull {
                        avgMedianDurations.add(
                            TimespanChartPoint(
                                value = duration,
                                from = metric.createdAt
                            )
                        )
                        avgMedianDuration.add(duration)
                        avgMedianParallelDuration.add(parallelDuration)
                        avgMedianParallelRate.add(parallelRate)
                        avgMedianCoverage.add(coverage)
                    }
            }

            modules.add(
                ModuleExecutionProcess(
                    path = module.path,
                    avgMedianDuration = MathUtils.longMedian(avgMedianDuration),
                    avgMedianParallelDuration = MathUtils.longMedian(avgMedianParallelDuration),
                    avgMedianParallelRate = MathUtils.floatMedian(avgMedianParallelRate).round(),
                    avgMedianCoverage = MathUtils.floatMedian(avgMedianCoverage).round(),
                    avgMedianDurations = avgMedianDurations.minimize(CHART_MAX_COLUMNS).mapToChartPoints(),
                    diffRate = diffRate
                )
            )

        }

        return report.apply {
            modulesExecutionProcessReport = ModulesExecutionProcessReport(
                modules = modules
            )
        }
    }

}
