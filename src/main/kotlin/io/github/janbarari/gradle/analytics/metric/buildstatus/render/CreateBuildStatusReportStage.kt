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

    override suspend fun process(input: Report): Report {
        return input.apply {
            buildStatusReport = BuildStatusReport(
                cumulativeOverallBuildProcessBySeconds = getCumulativeOverallBuildProcessInSeconds(),
                avgOverallBuildProcessBySeconds = getAvgOverallBuildProcessInSeconds(),
                totalBuildProcessCount = metrics.size,
                totalProjectModulesCount = modules.size,
                cumulativeDependencyResolveBySeconds = getCumulativeDependencyResolveBySeconds(),
                cumulativeParallelExecutionBySeconds = getCumulativeParallelExecutionBySeconds(),
                avgParallelExecutionRate = getAvgParallelExecutionRate(),
                totalFailedBuildCount = getTotalFailedBuildCount(),
                totalSucceedBuildCount = getTotalSuccessBuildCount(),
                avgCacheHitRate = getAvgCacheHitRate(),
                avgInitializationProcessByMillis = getAvgInitializationProcessByMillis(),
                avgConfigurationProcessByMillis = getAvgConfigurationProcessByMillis(),
                avgExecutionProcessBySeconds = getAvgExecutionProcessBySeconds()
            )
        }
    }

    fun getCumulativeOverallBuildProcessInSeconds(): Long {
        return metrics
            .filter { it.overallBuildProcessMetric.isNotNull() }
            .sumOf { metric ->
                metric.overallBuildProcessMetric!!.median.millisToSeconds()
            }
    }

    fun getAvgOverallBuildProcessInSeconds(): Long {
        return MathUtils.longMedian(
            metrics.filter { it.overallBuildProcessMetric.isNotNull() }
                .map { metric ->
                    metric.overallBuildProcessMetric!!.median.millisToSeconds()
                }
        )
    }

    fun getCumulativeDependencyResolveBySeconds(): Long {
        return metrics
            .filter { it.dependencyResolveProcessMetric.isNotNull() }
            .sumOf { metric ->
                metric.dependencyResolveProcessMetric!!.median.millisToSeconds()
            }
    }

    fun getCumulativeParallelExecutionBySeconds(): Long {
        return metrics
            .filter { it.executionProcessMetric.isNotNull() && it.parallelExecutionRateMetric.isNotNull() }
            .sumOf { metric ->
                MathUtils.sumWithPercentage(
                    metric.executionProcessMetric!!.median.millisToSeconds(),
                    metric.parallelExecutionRateMetric!!.medianRate.toInt()
                )
            }
    }

    fun getAvgParallelExecutionRate(): Float {
        return MathUtils.floatMedian(
            metrics.filter { it.parallelExecutionRateMetric.isNotNull() }
                .map { metric ->
                    metric.parallelExecutionRateMetric!!.medianRate.toFloat()
                }
        )
    }

    fun getTotalFailedBuildCount(): Int {
        return metrics
            .filter { it.successBuildRateMetric.isNotNull() }
            .sumOf { it.successBuildRateMetric!!.fails }
    }

    fun getTotalSuccessBuildCount(): Int {
        return metrics
            .filter { it.successBuildRateMetric.isNotNull() }
            .sumOf { it.successBuildRateMetric!!.successes }
    }

    fun getAvgCacheHitRate(): Float {
        return MathUtils.floatMedian(
            metrics
                .filter { it.cacheHitMetric.isNotNull() }
                .map { it.cacheHitMetric!!.rate.toFloat() }
        )
    }

    fun getAvgInitializationProcessByMillis(): Long {
        return MathUtils.longMedian(
            metrics.filter { it.initializationProcessMetric.isNotNull() }
                .map { metric ->
                    metric.initializationProcessMetric!!.median
                }
        )
    }

    fun getAvgConfigurationProcessByMillis(): Long {
        return MathUtils.longMedian(
            metrics.filter { it.configurationProcessMetric.isNotNull() }
                .map { metric ->
                    metric.configurationProcessMetric!!.median
                }
        )
    }

    fun getAvgExecutionProcessBySeconds(): Long {
        return MathUtils.longMedian(
            metrics.filter { it.executionProcessMetric.isNotNull() }
                .map { metric ->
                    metric.executionProcessMetric!!.median.millisToSeconds()
                }
        )
    }

}
