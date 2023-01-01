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

import io.github.janbarari.gradle.analytics.domain.model.TimespanPoint
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.report.ModuleExecutionProcess
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesExecutionProcessReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.diffPercentageOf
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.round
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.utils.MathUtils

class CreateModulesExecutionProcessReportStage(
    private val tower: Tower,
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    companion object {
        private val clazz = CreateModulesExecutionProcessReportStage::class.java
    }

    override suspend fun process(input: Report): Report {
        tower.i(clazz, "process()")
        val temp = metrics.lastOrNull()?.modules?.map { path ->
            var firstAvgMedianDuration: Long? = null
            var lastAvgMedianDuration: Long? = null
            var diffRate: Float? = null

            val avgMedianExecTimespanPoints = mutableListOf<TimespanPoint>()
            val avgMedianExecs = mutableListOf<Long>()
            val avgMedianParallelExecs = mutableListOf<Long>()
            val avgMedianParallelRates = mutableListOf<Float>()
            val avgMedianCoverageRates = mutableListOf<Float>()

            metrics.firstOrNull { metric ->
                metric.modulesExecutionProcessMetric.isNotNull()
            }.whenNotNull {
                modulesExecutionProcessMetric!!
                    .modules
                    .find { it.path == path }
                    .whenNotNull {
                        firstAvgMedianDuration = medianExecInMillis
                    }
            }

            metrics.lastOrNull { metric ->
                metric.modulesExecutionProcessMetric.isNotNull()
            }.whenNotNull {
                modulesExecutionProcessMetric!!
                    .modules
                    .find { it.path == path }
                    .whenNotNull {
                        lastAvgMedianDuration = medianExecInMillis
                    }
            }

            if (firstAvgMedianDuration.isNotNull() && lastAvgMedianDuration.isNotNull())
                diffRate = firstAvgMedianDuration!!.diffPercentageOf(lastAvgMedianDuration!!)

            metrics.filter { metric ->
                metric.modulesExecutionProcessMetric.isNotNull()
            }.forEach { metric ->
                metric.modulesExecutionProcessMetric!!
                    .modules
                    .find { it.path == path }
                    .whenNotNull {
                        avgMedianExecTimespanPoints.add(
                            TimespanPoint(
                                value = medianExecInMillis,
                                from = metric.createdAt
                            )
                        )
                        avgMedianExecs.add(medianExecInMillis)
                        avgMedianParallelExecs.add(medianParallelExecInMillis)
                        avgMedianParallelRates.add(parallelRate)
                        avgMedianCoverageRates.add(coverageRate)
                    }
            }

            ModuleExecutionProcess(
                path = path,
                avgMedianExecInMillis = MathUtils.longMedian(avgMedianExecs),
                avgMedianParallelExecInMillis = MathUtils.longMedian(avgMedianParallelExecs),
                avgMedianParallelRate = MathUtils.floatMedian(avgMedianParallelRates).round(),
                avgMedianCoverageRate = MathUtils.floatMedian(avgMedianCoverageRates).round(),
                avgMedianExecs = avgMedianExecTimespanPoints,
                diffRate = diffRate
            )
        } ?: emptyList()

        return input.apply {
            modulesExecutionProcessReport = ModulesExecutionProcessReport(modules = temp)
        }
    }

}
