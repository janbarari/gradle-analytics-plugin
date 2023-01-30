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
package io.github.janbarari.gradle.analytics.metric.modulesourcecount.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.SuspendStage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.utils.HtmlUtils

class RenderModulesSourceCountReportStage(
    private val tower: Tower,
    private val report: Report
) : SuspendStage<String, String> {

    companion object {
        private const val MODULES_SOURCE_COUNT_METRIC_TEMPLATE_ID = "%modules-source-count-metric%"
        private const val MODULES_SOURCE_COUNT_METRIC_TEMPLATE_FILE_NAME = "modules-source-count-metric-template"
        private val clazz = RenderModulesSourceCountReportStage::class.java
    }

    override suspend fun process(input: String): String {
        tower.i(clazz, "process()")
        if (report.modulesSourceCountReport.isNull())
            return input.replace(MODULES_SOURCE_COUNT_METRIC_TEMPLATE_ID, getEmptyRender())

        return input.replace(MODULES_SOURCE_COUNT_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Modules Source Count is not available!")
    }

    fun getMetricRender(): String {
        val totalSourceCount = report.modulesSourceCountReport?.totalSourceCount ?: 0

        var totalDiffRatioRender = "<td>-</td>"
        report.modulesSourceCountReport.whenNotNull {
            totalDiffRate.whenNotNull {
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
            report.modulesSourceCountReport?.values?.forEachIndexed { index, it ->
                var diffRatioRender = "<td>-</td>"
                it.diffRate.whenNotNull {
                    diffRatioRender = if (this > 0) {
                        "<td>+${this}%</td>"
                    } else if (this < 0){
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
                        <td>${it.coverageRate}%</td>
                        $diffRatioRender
                    </tr>
                """.trimIndent()
                )
            }
        }

        val moduleLabels = report.modulesSourceCountReport?.values?.map { "\"${it.path}\"" }
        val moduleValues = report.modulesSourceCountReport?.values?.map { it.value }

        var renderedTemplate = HtmlUtils.getTemplate(MODULES_SOURCE_COUNT_METRIC_TEMPLATE_FILE_NAME)
        renderedTemplate = renderedTemplate
            .replace("%table-data%", tableData)
                .replace("%total-source-count%", totalSourceCount.toString())
                .replace("%total-diff-rate%", totalDiffRatioRender)
                .replace("%module-labels%", moduleLabels.toString())
                .replace("%module-values%", moduleValues.toString())

        return renderedTemplate
    }

}
