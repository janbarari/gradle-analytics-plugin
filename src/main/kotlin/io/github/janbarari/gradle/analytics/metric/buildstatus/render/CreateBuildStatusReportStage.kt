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

import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.report.BuildStatusReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.utils.MathUtils

class CreateBuildStatusReportStage(
    private val modulesPath: List<ModulePath>,
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    @Suppress("LongMethod")
    override suspend fun process(input: Report): Report {
        val cumulativeBuildProcessDuration = metrics.filter { it.overallBuildProcessMetric.isNotNull() }.sumOf {
                ensureNotNull(it.overallBuildProcessMetric).median
            }

        val avgBuildProcessDuration: Long =
            MathUtils.longMedian(metrics.filter { it.overallBuildProcessMetric.isNotNull() }.map {
                    it.overallBuildProcessMetric!!.median / 1000L
                })

        val totalBuildProcessCount = metrics.size

        val totalModulesCount = modulesPath.size

        val cumulativeDependencyResolveDuration = metrics.filter { it.dependencyResolveProcessMetric.isNotNull() }
            .sumOf { it.dependencyResolveProcessMetric!!.median / 1000L }

        val cumulativeParallelExecutionDuration =
            metrics.filter { it.executionProcessMetric.isNotNull() && it.parallelExecutionRateMetric.isNotNull() }.sumOf {
                    MathUtils.sumWithPercentage(
                        it.executionProcessMetric!!.median / 1000L, it.parallelExecutionRateMetric!!.rate.toInt()
                    )
                }

        val avgParallelExecutionRate: Float =
            MathUtils.floatMedian(metrics.filter { it.parallelExecutionRateMetric.isNotNull() }.map {
                    it.parallelExecutionRateMetric!!.rate.toFloat()
                })

        val totalFailedBuildCount = metrics.filter {
                it.successBuildRateMetric.isNotNull()
            }.sumOf {
                it.successBuildRateMetric!!.fails
            }

        val totalSuccessBuildCount = metrics.filter {
                it.successBuildRateMetric.isNotNull()
            }.sumOf {
                it.successBuildRateMetric!!.successes
            }

        val avgCacheHitRate: Float =
            MathUtils.floatMedian(metrics.filter { it.cacheHitMetric.isNotNull() }.map {
                it.cacheHitMetric!!.rate.toFloat()
            })

        val avgInitializationProcessDuration: Long =
            MathUtils.longMedian(metrics.filter { it.initializationProcessMetric.isNotNull() }.map {
                it.initializationProcessMetric!!.median
            })

        val avgConfigurationProcessDuration: Long =
            MathUtils.longMedian(metrics.filter { it.configurationProcessMetric.isNotNull() }.map {
                it.configurationProcessMetric!!.median
            })

        val avgExecutionProcessDuration: Long =
            MathUtils.longMedian(metrics.filter { it.executionProcessMetric.isNotNull() }.map {
                it.executionProcessMetric!!.median / 1000L
            })

        return input.apply {
            buildStatusReport = BuildStatusReport(
                cumulativeBuildProcessDuration = cumulativeBuildProcessDuration,
                avgBuildProcessDuration = avgBuildProcessDuration,
                totalBuildProcessCount = totalBuildProcessCount,
                totalModulesCount = totalModulesCount,
                cumulativeDependencyResolveDuration = cumulativeDependencyResolveDuration,
                cumulativeParallelExecutionDuration = cumulativeParallelExecutionDuration,
                avgParallelExecutionRate = avgParallelExecutionRate,
                totalFailedBuildCount = totalFailedBuildCount,
                totalSuccessBuildCount = totalSuccessBuildCount,
                avgCacheHitRate = avgCacheHitRate,
                avgInitializationProcessDuration = avgInitializationProcessDuration,
                avgConfigurationProcessDuration = avgConfigurationProcessDuration,
                avgExecutionProcessDuration = avgExecutionProcessDuration
            )
        }
    }

}
