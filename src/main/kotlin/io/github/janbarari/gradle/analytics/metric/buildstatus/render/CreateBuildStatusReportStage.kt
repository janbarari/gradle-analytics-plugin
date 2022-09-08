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
package io.github.janbarari.gradle.analytics.metric.buildstatus.render

import io.github.janbarari.gradle.analytics.domain.model.Module
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.report.BuildStatusReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.millisToSeconds
import io.github.janbarari.gradle.utils.MathUtils

class CreateBuildStatusReportStage(
    private val modules: List<Module>,
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    @Suppress("LongMethod")
    override suspend fun process(input: Report): Report {
        val cumulativeOverallBuildProcessDuration = metrics
            .filter { it.overallBuildProcessMetric.isNotNull() }
            .sumOf { metric ->
                metric.overallBuildProcessMetric!!.median.millisToSeconds()
            }

        val avgOverallBuildProcessDuration: Long = MathUtils.longMedian(
            metrics.filter { it.overallBuildProcessMetric.isNotNull() }
                .map { metric ->
                    metric.overallBuildProcessMetric!!.median.millisToSeconds()
                }
        )

        val totalBuildProcessCount = metrics.size

        val totalModulesCount = modules.size

        val cumulativeDependencyResolveBySeconds = metrics.filter { it.dependencyResolveProcessMetric.isNotNull() }
            .sumOf { metric ->
                metric.dependencyResolveProcessMetric!!.median.millisToSeconds()
            }

        val cumulativeParallelExecutionBySeconds =
            metrics.filter { it.executionProcessMetric.isNotNull() && it.parallelExecutionRateMetric.isNotNull() }
                .sumOf { metric ->
                    MathUtils.sumWithPercentage(
                        metric.executionProcessMetric!!.median.millisToSeconds(),
                        metric.parallelExecutionRateMetric!!.medianRate.toInt()
                    )
                }

        val avgParallelExecutionRate: Float = MathUtils.floatMedian(
            metrics.filter { it.parallelExecutionRateMetric.isNotNull() }
                .map { metric ->
                    metric.parallelExecutionRateMetric!!.medianRate.toFloat()
                }
        )

        val totalFailedBuildCount = metrics.filter {
            it.successBuildRateMetric.isNotNull()
        }.sumOf { metric ->
            metric.successBuildRateMetric!!.fails
        }

        val totalSuccessBuildCount = metrics.filter {
            it.successBuildRateMetric.isNotNull()
        }.sumOf { metric ->
            metric.successBuildRateMetric!!.successes
        }

        val avgCacheHitRate: Float = MathUtils.floatMedian(
            metrics.filter { it.cacheHitMetric.isNotNull() }
                .map { metric ->
                    metric.cacheHitMetric!!.rate.toFloat()
                }
        )

        val avgInitializationProcessByMillis: Long = MathUtils.longMedian(
            metrics.filter { it.initializationProcessMetric.isNotNull() }
                .map { metric ->
                    metric.initializationProcessMetric!!.median
                }
        )

        val avgConfigurationProcessByMillis: Long = MathUtils.longMedian(
            metrics.filter { it.configurationProcessMetric.isNotNull() }
                .map { metric ->
                    metric.configurationProcessMetric!!.median
                }
        )

        val avgExecutionProcessBySeconds: Long = MathUtils.longMedian(
            metrics.filter { it.executionProcessMetric.isNotNull() }
                .map { metric ->
                    metric.executionProcessMetric!!.median.millisToSeconds()
                }
        )

        return input.apply {
            buildStatusReport = BuildStatusReport(
                cumulativeOverallBuildProcessBySeconds = cumulativeOverallBuildProcessDuration,
                avgOverallBuildProcessBySeconds = avgOverallBuildProcessDuration,
                totalBuildProcessCount = totalBuildProcessCount,
                totalProjectModulesCount = totalModulesCount,
                cumulativeDependencyResolveBySeconds = cumulativeDependencyResolveBySeconds,
                cumulativeParallelExecutionBySeconds = cumulativeParallelExecutionBySeconds,
                avgParallelExecutionRate = avgParallelExecutionRate,
                totalFailedBuildCount = totalFailedBuildCount,
                totalSucceedBuildCount = totalSuccessBuildCount,
                avgCacheHitRate = avgCacheHitRate,
                avgInitializationProcessByMillis = avgInitializationProcessByMillis,
                avgConfigurationProcessByMillis = avgConfigurationProcessByMillis,
                avgExecutionProcessBySeconds = avgExecutionProcessBySeconds
            )
        }
    }

}
