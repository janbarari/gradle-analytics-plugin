/**
 * MIT License
 * Copyright (c) 2024 Mehdi Janbarari (@janbarari)
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

import io.github.janbarari.gradle.analytics.domain.model.report.ModulesDependencyGraphReportJsonAdapter
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.SuspendStage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.toArrayRender
import io.github.janbarari.gradle.extension.toRealPath
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.utils.HtmlUtils
import java.io.File

class RenderModulesDependencyGraphReportStage(
    private val tower: Tower,
    private val modulesDependencyGraphReportJsonAdapter: ModulesDependencyGraphReportJsonAdapter,
    private val report: Report,
    private val outputPath: String,
    private val projectName: String,
    private val excludeModules: Set<String>
): SuspendStage<String, String> {

    companion object {
        private const val MODULES_DEPENDENCY_GRAPH_METRIC_TEMPLATE_ID = "%modules-dependency-graph-metric%"
        private const val MODULES_DEPENDENCY_GRAPH_METRIC_EXTERNAL_TEMPLATE_FILE_NAME =
            "modules-dependency-graph-metric-external-template"
        private const val MODULES_DEPENDENCY_GRAPH_TEMPLATE_FILE_NAME = "modules-dependency-graph-template"
        private val clazz = RenderModulesDependencyGraphReportStage::class.java
    }

    override suspend fun process(input: String): String {
        tower.i(clazz, "process()")
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
            result = HtmlUtils.getTemplate(MODULES_DEPENDENCY_GRAPH_METRIC_EXTERNAL_TEMPLATE_FILE_NAME)

            var externalGraphRender = HtmlUtils.getTemplate(MODULES_DEPENDENCY_GRAPH_TEMPLATE_FILE_NAME)

            externalGraphRender = externalGraphRender
                .replace("%root-project-name%", projectName)
                .replace("%exclude-modules%", excludeModules.toList().toArrayRender())
                .replace("%graph-json%", modulesDependencyGraphReportJsonAdapter.toJson(
                    report.modulesDependencyGraphReport
                ))

            val savePath = "${outputPath.toRealPath()}/gradle-analytics-plugin"
            val directory = File(savePath)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            File("$savePath/modules-dependency-graph.html").writeText(externalGraphRender)
        }
        return result
    }
}
