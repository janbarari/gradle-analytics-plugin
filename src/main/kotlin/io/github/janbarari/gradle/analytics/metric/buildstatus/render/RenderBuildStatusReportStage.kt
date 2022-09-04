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

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.round
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.DateTimeUtils
import io.github.janbarari.gradle.utils.HtmlUtils

class RenderBuildStatusReportStage(
    private val report: Report
): Stage<String, String> {

    companion object {
        private const val BUILD_STATUS_METRIC_TEMPLATE_ID = "%build-status-metric%"
        private const val BUILD_STATUS_METRIC_TEMPLATE_FILENAME = "build-status-metric-template"
    }

    override suspend fun process(input: String): String {
        if (report.buildStatusReport.isNull())
            return input.replace(BUILD_STATUS_METRIC_TEMPLATE_ID, getEmptyRender())

        return input.replace(BUILD_STATUS_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Build Status is not available!")
    }

    fun getMetricRender(): String {
        var renderedTemplate = HtmlUtils.getTemplate(BUILD_STATUS_METRIC_TEMPLATE_FILENAME)
        report.buildStatusReport.whenNotNull {
            renderedTemplate = renderedTemplate
                .replace("%cumulative-build-process-duration%",
                    DateTimeUtils.convertSecondsToHumanReadableTime(cumulativeOverallBuildProcessBySeconds)
                )
                .replace("%avg-build-process-duration%",
                    DateTimeUtils.convertSecondsToHumanReadableTime(avgOverallBuildProcessBySeconds)
                )
                .replace("%total-build-process-count%", totalBuildProcessCount.toString())
                .replace("%total-modules-count%", totalProjectModulesCount.toString())
                .replace("%cumulative-parallel-exec-duration%",
                    DateTimeUtils.convertSecondsToHumanReadableTime(cumulativeParallelExecutionBySeconds)
                )
                .replace("%avg-parallel-exec-rate%", "${avgParallelExecutionRate.round()}%")
                .replace("%total-succeed-build-count%", "$totalSucceedBuildCount")
                .replace("%total-failed-build-count%", "$totalFailedBuildCount")
                .replace("%avg-cache-hit-rate%", "${avgCacheHitRate.round()}%")
                .replace("%cumulative-dependency-resolve-duration%",
                    DateTimeUtils.convertSecondsToHumanReadableTime(cumulativeDependencyResolveBySeconds)
                )
                .replace("%avg-initialization-process-duration%", "${avgInitializationProcessByMillis}ms")
                .replace("%avg-configuration-process-duration%", "${avgConfigurationProcessByMillis}ms")
                .replace("%avg-execution-process-duration%", "${avgExecutionProcessBySeconds}s")
        }
        return renderedTemplate
    }



}
