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
package io.github.janbarari.gradle.analytics.metric.configurationprocess.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.SuspendStage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.mapToChartPoints
import io.github.janbarari.gradle.extension.maxValue
import io.github.janbarari.gradle.extension.minValue
import io.github.janbarari.gradle.extension.minimize
import io.github.janbarari.gradle.extension.toArrayRender
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.utils.HtmlUtils
import io.github.janbarari.gradle.utils.MathUtils

/**
 * Generates html result for [io.github.janbarari.gradle.analytics.domain.model.report.ConfigurationProcessReport].
 */
class RenderConfigurationProcessReportStage(
    private val tower: Tower,
    private val report: Report
) : SuspendStage<String, String> {

    companion object {
        private const val CHART_MAX_COLUMNS = 12
        private const val CHART_SUGGESTED_MIN_MAX_PERCENTAGE = 30
        private const val CONFIGURATION_METRIC_TEMPLATE_ID = "%configuration-process-metric%"
        private const val CONFIGURATION_METRIC_TEMPLATE_FILE_NAME = "configuration-process-metric-template"
        private val clazz = RenderConfigurationProcessReportStage::class.java
    }

    override suspend fun process(input: String): String {
        tower.i(clazz, "process()")
        if (report.configurationProcessReport.isNull()) {
            return input.replace(CONFIGURATION_METRIC_TEMPLATE_ID, getEmptyRender())
        }

        return input.replace(CONFIGURATION_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getMetricRender(): String {
        var renderedTemplate = HtmlUtils.getTemplate(CONFIGURATION_METRIC_TEMPLATE_FILE_NAME)
        report.configurationProcessReport.whenNotNull {
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

            val maximumValue = Math.max(medianValues.maxValue(), meanValues.maxValue())
            val minimumValue = Math.min(medianValues.minValue(), meanValues.minValue())
            val chartSuggestedMaxValue = MathUtils.sumWithPercentage(maximumValue, CHART_SUGGESTED_MIN_MAX_PERCENTAGE)
            val chartSuggestedMinValue = MathUtils.deductWithPercentage(minimumValue, CHART_SUGGESTED_MIN_MAX_PERCENTAGE)

            renderedTemplate = renderedTemplate
                .replace("%suggested-max-value%", chartSuggestedMaxValue.toString())
                .replace("%suggested-min-value%", chartSuggestedMinValue.toString())
                .replace("%chart-median-values%", medianChartValues)
                .replace("%chart-mean-values%", meanChartValues)
                .replace("%chart-labels%", chartLabels)
        }
        return renderedTemplate
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Configuration Process is not available!")
    }
}
