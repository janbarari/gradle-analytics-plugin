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
package io.github.janbarari.gradle.analytics.metric.modulesdependencygraph.report

import io.github.janbarari.gradle.analytics.domain.model.ModuleDependency
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.toRealPath
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.HtmlUtils
import java.io.File


class RenderModulesDependencyGraphReportStage(
    private val report: Report,
    private val outputPath: String,
    private val projectName: String
): Stage<String, String> {

    companion object {
        private const val MODULES_DEPENDENCY_GRAPH_METRIC_TEMPLATE_ID = "%modules-dependency-graph-metric%"
        private const val MODULES_DEPENDENCY_GRAPH_METRIC_INTERNAL_TEMPLATE_FILE_NAME =
            "modules-dependency-graph-metric-internal-template"
        private const val MODULES_DEPENDENCY_GRAPH_METRIC_EXTERNAL_TEMPLATE_FILE_NAME =
            "modules-dependency-graph-metric-external-template"
        private const val MODULES_DEPENDENCY_GRAPH_TEMPLATE_FILE_NAME = "modules-dependency-graph-template"
        private const val MAXIMUM_ALLOWED_MODULES_TO_RENDER_INTERNALLY = 16
    }

    override suspend fun process(input: String): String {
        if (report.modulesDependencyGraphReport.isNull()) {
            return input.replace(MODULES_DEPENDENCY_GRAPH_METRIC_TEMPLATE_ID, getEmptyRender())
        }

        return input.replace(MODULES_DEPENDENCY_GRAPH_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Modules Dependency Graph is not available!")
    }

    fun getMetricRender(): String {
        var result = ""
        report.modulesDependencyGraphReport.whenNotNull {
            if (modules.size <= MAXIMUM_ALLOWED_MODULES_TO_RENDER_INTERNALLY) {
                result = HtmlUtils.getTemplate(MODULES_DEPENDENCY_GRAPH_METRIC_INTERNAL_TEMPLATE_FILE_NAME)
                result = result.replace("%mermaid-commands%", generateMermaidCommands(dependencies))
            } else {
                result = HtmlUtils.getTemplate(MODULES_DEPENDENCY_GRAPH_METRIC_EXTERNAL_TEMPLATE_FILE_NAME)

                var externalGraphRender = HtmlUtils.getTemplate(MODULES_DEPENDENCY_GRAPH_TEMPLATE_FILE_NAME)
                externalGraphRender = externalGraphRender
                    .replace("%root-project-name%", projectName)
                    .replace("%max-text-size%", "200000")
                    .replace("%mermaid-commands%", generateMermaidCommands(dependencies))

                val savePath = "${outputPath.toRealPath()}/gradle-analytics-plugin"
                val directory = File(savePath)
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                File("$savePath/modules-dependency-graph.html").writeText(externalGraphRender)
            }
        }
        return result
    }

    fun generateMermaidCommands(dependencies: List<ModuleDependency>): String {
        return buildString {
            appendLine()
            dependencies.whenEach {
                val type = when(configuration) {
                    "api" -> "api"
                    "implementation" -> "impl"
                    else -> configuration
                }

                val pathColor = dependencies.filter { it.dependency == dependency }.size
                var heatmapColor = ":::blue"
                if (pathColor in 3 .. 4) {
                    heatmapColor = ":::yellow"
                } else if (pathColor in 5 .. 6) {
                    heatmapColor = ":::orange"
                } else if (pathColor > 6) {
                    heatmapColor = ":::red"
                }

                append("\t$path ---> |$type| $dependency$heatmapColor")
                appendLine()
            }
        }
    }

}
