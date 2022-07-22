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
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.getTextResourceContent
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.whenNotNull

class RenderModulesSourceCountStage(
    private val report: Report
) : Stage<String, String> {

    override suspend fun process(input: String): String {
        if (report.modulesSourceCountReport.isNull()) return input.replace(
            "%modules-source-count-metric%",
            "<p>Modules Source Count Metric is not available!</p><div class=\"space\"></div>"
        )

        val totalSourceCount = report.modulesSourceCountReport?.totalSourceCount ?: 0

        var totalDiffRatio = "<td>-</td>"
        report.modulesSourceCountReport.whenNotNull {
            this.totalDiffRatio.whenNotNull {
                totalDiffRatio = if (this > 0) {
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
                var diffRatio = "<td>-</td>"
                it.diffRatio.whenNotNull {
                    diffRatio = if (this > 0) {
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
                        <td>${it.coverage}%</td>
                        $diffRatio
                    </tr>
                """.trimIndent()
                )
            }
        }

        val moduleLabels = report.modulesSourceCountReport?.values?.map { "\"${it.path}\"" }
        val moduleValues = report.modulesSourceCountReport?.values?.map { it.value }

        var template = getTextResourceContent("modules-source-count-metric-template.html")
        template =
            template.replace("%table-data%", tableData)
                .replace("%total-source-count%", totalSourceCount.toString())
                .replace("%total-diff-ratio%", totalDiffRatio)
                .replace("%module-labels%", moduleLabels.toString())
                .replace("%module-values%", moduleValues.toString())

        return input.replace("%modules-source-count-metric%", template)
    }

}
