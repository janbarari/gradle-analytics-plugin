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
package io.github.janbarari.gradle.analytics.metric.successbuildrate.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.mapToChartPoints
import io.github.janbarari.gradle.extension.minimize
import io.github.janbarari.gradle.extension.toArrayRender
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.utils.HtmlUtils

class RenderSuccessBuildRateReportStage(
    private val tower: Tower,
    private val report: Report
): Stage<String, String> {

    companion object {
        private const val CHART_MAX_COLUMNS = 12
        private const val BUILD_SUCCESS_RATIO_METRIC_TEMPLATE_ID = "%success-build-rate-metric%"
        private const val BUILD_SUCCESS_RATIO_METRIC_TEMPLATE_FILE_NAME = "success-build-rate-metric-template"
        private val clazz = RenderSuccessBuildRateReportStage::class.java
    }

    override suspend fun process(input: String): String {
        tower.i(clazz, "process()")
        if (report.successBuildRateReport.isNull())
            return input.replace(BUILD_SUCCESS_RATIO_METRIC_TEMPLATE_ID, getEmptyRender())

        return input.replace(BUILD_SUCCESS_RATIO_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Success Build Rate is not available!")
    }

    fun getMetricRender(): String {
        var renderedTemplate = HtmlUtils.getTemplate(BUILD_SUCCESS_RATIO_METRIC_TEMPLATE_FILE_NAME)
        report.successBuildRateReport.whenNotNull {
            val medianChartValues = medianValues
                .minimize(CHART_MAX_COLUMNS)
                .mapToChartPoints()
                .map { it.value }
                .toIntList()
                .toString()

            val meanChartValues = meanValues
                .minimize(CHART_MAX_COLUMNS)
                .mapToChartPoints()
                .map { it.value }
                .toIntList()
                .toString()

            val chartLabels = medianValues
                .minimize(CHART_MAX_COLUMNS)
                .mapToChartPoints()
                .map { it.description }
                .toArrayRender()

            renderedTemplate = renderedTemplate
                .replace("%chart-median-values%", medianChartValues)
                .replace("%chart-mean-values%", meanChartValues)
                .replace("%chart-labels%", chartLabels)
        }
        return renderedTemplate
    }

}
