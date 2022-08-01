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
package io.github.janbarari.gradle.analytics.metric.initialization.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.toArrayString
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.HtmlUtils
import io.github.janbarari.gradle.utils.MathUtils

class RenderInitializationReportStage(
    private val report: Report
) : Stage<String, String> {

    companion object {
        private const val CHART_SUGGESTED_MIN_MAX_PERCENTAGE = 30
        private const val INITIALIZATION_METRIC_TEMPLATE_ID = "%initialization-metric%"
        private const val INITIALIZATION_METRIC_TEMPLATE_FILE_NAME = "initialization-metric-template"
    }

    override suspend fun process(input: String): String {
        if (report.initializationProcessReport.isNull())
            return input.replace(INITIALIZATION_METRIC_TEMPLATE_ID, getEmptyRender())

        return input.replace(INITIALIZATION_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Initialization chart is not available!")
    }

    fun getMetricRender(): String {
        var renderedTemplate = HtmlUtils.getTemplate(INITIALIZATION_METRIC_TEMPLATE_FILE_NAME)
        report.initializationProcessReport.whenNotNull {
            val chartValues = medianValues.map { it.value }
                .toIntList()
                .toString()

            val chartLabels = medianValues.map { it.description }
                .toArrayString()

            val chartSuggestedMaxValue = MathUtils.sumWithPercentage(suggestedMaxValue, CHART_SUGGESTED_MIN_MAX_PERCENTAGE)
            val chartSuggestedMinValue = MathUtils.deductWithPercentage(suggestedMinValue, CHART_SUGGESTED_MIN_MAX_PERCENTAGE)

            renderedTemplate = renderedTemplate
                .replace("%initialization-suggested-max-value%", chartSuggestedMaxValue.toString())
                .replace("%initialization-suggested-min-value%", chartSuggestedMinValue.toString())
                .replace("%initialization-median-values%", chartValues)
                .replace("%initialization-median-labels%", chartLabels)
        }
        return renderedTemplate
    }

}
