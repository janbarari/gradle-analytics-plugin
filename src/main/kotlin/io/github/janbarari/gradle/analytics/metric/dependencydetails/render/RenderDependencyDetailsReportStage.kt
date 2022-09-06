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
package io.github.janbarari.gradle.analytics.metric.dependencydetails.render

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.toArrayString
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.HtmlUtils

class RenderDependencyDetailsReportStage(
    private val report: Report
) : Stage<String, String> {

    companion object {
        private const val DEPENDENCY_DETAILS_METRIC_TEMPLATE_ID = "%dependency-details-metric%"
        private const val DEPENDENCY_DETAILS_METRIC_TEMPLATE_FILE_NAME = "dependency-details-metric-template"
    }

    override suspend fun process(input: String): String {
        if (report.dependencyDetailsReport.isNull()) return input.replace(
            DEPENDENCY_DETAILS_METRIC_TEMPLATE_ID, getEmptyRender()
        )

        return input.replace(DEPENDENCY_DETAILS_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Dependency Details is not available!")
    }

    fun getMetricRender(): String {
        var renderedTemplate = HtmlUtils.getTemplate(DEPENDENCY_DETAILS_METRIC_TEMPLATE_FILE_NAME)
        report.dependencyDetailsReport.whenNotNull {
            val chartLabels = mutableListOf<String>()
            val chartDataset = mutableListOf<Long>()
            dependencies.whenEach {
                chartLabels.add("$moduleGroup:$moduleName")
                chartDataset.add(sizeInKb)
            }

            val tableDataset = buildString {
                dependencies
                    .filter { it.sizeInKb > 0 }
                    .sortedByDescending { it.sizeInKb }
                    .forEachIndexed { index, dependency ->
                        append("<tr>")
                        append("<td>${index + 1}</td>")
                        append("<td>${dependency.name}</td>")
                        append("<td>${dependency.sizeInKb}kb</td>")
                        append("</tr>")
                    }
            }

            renderedTemplate = if (chartDataset.sum() > 1000) {
                renderedTemplate.replace("%cumulative-dependencies-size%", "%sMb".format((chartDataset.sum() / 1024)))
            } else {
                renderedTemplate.replace("%cumulative-dependencies-size%", "%skb".format(chartDataset.sum()))
            }

            renderedTemplate = renderedTemplate
                    .replace("%table-dataset%", tableDataset)
                    .replace("%chart-labels%", chartLabels.toArrayString())
                    .replace("%chart-dataset%", chartDataset.toString())
        }
        return renderedTemplate
    }

}
