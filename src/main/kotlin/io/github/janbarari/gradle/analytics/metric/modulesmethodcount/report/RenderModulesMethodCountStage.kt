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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.metric.modulesmethodcount.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.HtmlUtils

class RenderModulesMethodCountStage(
    private val report: Report
) : Stage<String, String> {

    companion object {
        private const val MODULES_METHOD_COUNT_METRIC_TEMPLATE_ID = "%modules-method-count-metric%"
        private const val MODULES_METHOD_COUNT_METRIC_TEMPLATE_FILE_NAME = "modules-method-count-metric-template"
    }

    override suspend fun process(input: String): String {
        if (report.modulesMethodCountReport.isNull())
            return input.replace(MODULES_METHOD_COUNT_METRIC_TEMPLATE_ID, getEmptyRender())

        return input.replace(MODULES_METHOD_COUNT_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Modules method count metric is not available!")
    }

    fun getMetricRender(): String {
        val totalMethodCount = report.modulesMethodCountReport?.totalMethodCount ?: 0

        var totalDiffRatioRender = "<td>-</td>"
        report.modulesMethodCountReport.whenNotNull {
            totalDiffRatio.whenNotNull {
                totalDiffRatioRender = if (this > 0) {
                    "<td>+${this}%</td>"
                } else if (this < 0) {
                    "<td>${this}%</td>"
                } else {
                    "<td>Equals</td>"
                }
            }
        }

        val tableData = buildString {
            report.modulesMethodCountReport?.values?.forEachIndexed { index, it ->
                var diffRatioRender = "<td>-</td>"
                it.diffRatio.whenNotNull {
                    diffRatioRender = if (this > 0) {
                        "<td>+${this}%</td>"
                    } else if (this < 0) {
                        "<td>${this}%</td>"
                    } else {
                        "<td>Equals</td>"
                    }
                }
                append(
                    """
                    <tr>
                        <td>${index + 1}</td>
                        <td>${it.path}</td>
                        <td>${it.value}</td>
                        <td>${it.coverage}%</td>
                        $diffRatioRender
                    </tr>
                """.trimIndent()
                )
            }
        }

        val moduleLabels = report.modulesMethodCountReport?.values?.map { "\"${it.path}\"" }
        val moduleValues = report.modulesMethodCountReport?.values?.map { it.value }

        var renderedTemplate = HtmlUtils.getTemplate(MODULES_METHOD_COUNT_METRIC_TEMPLATE_FILE_NAME)
        renderedTemplate = renderedTemplate
            .replace("%table-data%", tableData)
            .replace("%total-method-count%", totalMethodCount.toString())
            .replace("%total-diff-ratio%", totalDiffRatioRender)
            .replace("%module-labels%", moduleLabels.toString())
            .replace("%module-values%", moduleValues.toString())

        return renderedTemplate
    }

}
