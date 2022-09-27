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
package io.github.janbarari.gradle.analytics.metric.modulessourcesize.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.HtmlUtils

class RenderModulesSourceSizeReportStage(
    private val report: Report
) : Stage<String, String> {

    companion object {
        private const val MODULES_METHOD_COUNT_METRIC_TEMPLATE_ID = "%modules-source-size-metric%"
        private const val MODULES_METHOD_COUNT_METRIC_TEMPLATE_FILE_NAME = "modules-source-size-metric-template"
    }

    override suspend fun process(input: String): String {
        if (report.modulesSourceSizeReport.isNull())
            return input.replace(MODULES_METHOD_COUNT_METRIC_TEMPLATE_ID, getEmptyRender())

        return input.replace(MODULES_METHOD_COUNT_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Modules Source Size is not available!")
    }

    fun getMetricRender(): String {
        val totalSourceSizeByKb = report.modulesSourceSizeReport?.totalSourceSizeInKb ?: 0

        var totalDiffRatioRender = "<td>-</td>"
        report.modulesSourceSizeReport.whenNotNull {
            totalDiffRate.whenNotNull {
                totalDiffRatioRender = if (this > 0) {
                    "<td>+${this}%</td>"
                } else if (this < 0) {
                    "<td>-${this}%</td>"
                } else {
                    "<td>Equals</td>"
                }
            }
        }

        val tableData = buildString {
            report.modulesSourceSizeReport?.values?.forEachIndexed { index, it ->
                var diffRatioRender = "<td>-</td>"
                it.diffRate.whenNotNull {
                    diffRatioRender = if (this > 0) {
                        "<td>+${this}%</td>"
                    } else if (this < 0) {
                        "<td>-${this}%</td>"
                    } else {
                        "<td>Equals</td>"
                    }
                }
                append(
                    """
                    <tr>
                        <td>${index + 1}</td>
                        <td>${it.path}</td>
                        <td>${it.sizeInKb}kb</td>
                        <td>${it.coverageRate}%</td>
                        $diffRatioRender
                    </tr>
                """.trimIndent()
                )
            }
        }

        val moduleLabels = report.modulesSourceSizeReport?.values?.map { "\"${it.path}\"" }
        val moduleValues = report.modulesSourceSizeReport?.values?.map { it.sizeInKb }

        var renderedTemplate = HtmlUtils.getTemplate(MODULES_METHOD_COUNT_METRIC_TEMPLATE_FILE_NAME)
        renderedTemplate = renderedTemplate
            .replace("%table-data%", tableData)
            .replace("%total-source-size%", totalSourceSizeByKb.toString())
            .replace("%total-diff-rate%", totalDiffRatioRender)
            .replace("%module-labels%", moduleLabels.toString())
            .replace("%module-values%", moduleValues.toString())

        return renderedTemplate
    }

}
