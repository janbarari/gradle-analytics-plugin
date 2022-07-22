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
package io.github.janbarari.gradle.analytics.metric.configuration.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.getTextResourceContent
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.removeLastChar
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.utils.MathUtils

/**
 * Generates html result for [io.github.janbarari.gradle.analytics.domain.model.ConfigurationReport].
 */
class RenderConfigurationReportStage(
    private val report: Report
) : Stage<String, String> {

    override suspend fun process(input: String): String {
        if (report.configurationReport.isNull()) {
            return input.replace("%configuration-metric%", getEmptyRender())
        }
        return input.replace("%configuration-metric%", getMetricRender())
    }

    fun getMetricRender(): String {
        val chartPoints = ensureNotNull(report.configurationReport)
            .values
            .map { it.value }
            .toIntList()

        val chartPointLabels = StringBuilder()
        ensureNotNull(report.configurationReport)
            .values
            .map { it.description }
            .whenEach {
                chartPointLabels.append("\"$this\"").append(",")
            }.also {
                //because the last item should not have ',' separator.
                chartPointLabels.removeLastChar()
            }

        val suggestedMaxValue = MathUtils.sumWithPercentage(
            ensureNotNull(report.configurationReport).maxValue,
            30
        )

        val suggestedMinValue = MathUtils.deductWithPercentage(
            ensureNotNull(report.configurationReport).minValue,
            30
        )

        return getTextResourceContent("configuration-metric-template.html")
            .replace("%configuration-max-value%", suggestedMaxValue.toString())
            .replace("%configuration-min-value%", suggestedMinValue.toString())
            .replace("%configuration-median-values%", chartPoints.toString())
            .replace("%configuration-median-labels%", chartPointLabels.toString())
    }

    private fun getEmptyRender(): String {
        return "<p>Configuration Median Chart is not available!</p><div class=\"space\"></div>"
    }

}
