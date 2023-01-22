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
package io.github.janbarari.gradle.analytics.metric.redundantdependencygraph.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.SuspendStage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.utils.HtmlUtils

class RenderRedundantDependencyGraphReportStage(
    private val tower: Tower,
    private val report: Report
): SuspendStage<String, String> {

    companion object {
        private const val REDUNDANT_DEPENDENCY_GRAPH_METRIC_TEMPLATE_ID = "%redundant-dependency-graph-metric%"
        private const val REDUNDANT_DEPENDENCY_GRAPH_METRIC_TEMPLATE_FILENAME =
            "redundant-dependency-graph-metric-template"
        private val clazz = RenderRedundantDependencyGraphReportStage::class.java
    }

    override suspend fun process(input: String): String {
        tower.i(clazz, "process()")
        if (report.redundantDependencyGraphReport.isNull())
            return input.replace(REDUNDANT_DEPENDENCY_GRAPH_METRIC_TEMPLATE_ID, getEmptyRender())

        return input.replace(REDUNDANT_DEPENDENCY_GRAPH_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Redundant dependency graph is not available!")
    }

    fun getMetricRender(): String {
        var renderedTemplate = HtmlUtils.getTemplate(REDUNDANT_DEPENDENCY_GRAPH_METRIC_TEMPLATE_FILENAME)
        report.redundantDependencyGraphReport.whenNotNull {
            val redundantGraphsRender = buildString {
                if (redundantDependencies.isEmpty()) {
                    append("<p>No redundant dependency graph was found.</p>")
                }
                redundantDependencies.forEach { redundantDependency ->
                    append("<div class=\"redundant-dependency-item\">")
                    append("\n")
                    append("${redundantDependency.target.path} --> " +
                            "|${redundantDependency.target.configuration}|" +
                            " ${redundantDependency.target.dependency}")
                    append("\n")
                    append("<span class=\"gray-1\">is redundant since")
                    append("\n")
                    append("<span style=\"color: black;\">" + redundantDependency.target.dependency + "</span>")
                    append("\n")
                    append("is already accessible by:</span>")
                    append("<div class=\"space-extra-small\"></div>")
                    append("\n")
                    redundantDependency.reasons.forEach { reason ->
                        append(
                            "<span style=\"color: black;\">" +
                                "&emsp;&emsp; ${reason.path} --> |${reason.configuration}| ${reason.dependency}" +
                                "</span>" +
                                "<br>"
                        )
                    }
                    append("</div>")
                    append("\n")
                }
            }

            renderedTemplate = renderedTemplate
                .replace("%redundant-graphs%", redundantGraphsRender)
        }
        return renderedTemplate
    }

}
