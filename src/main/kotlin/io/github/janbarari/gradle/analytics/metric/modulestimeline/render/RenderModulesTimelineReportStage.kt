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
package io.github.janbarari.gradle.analytics.metric.modulestimeline.render

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.HtmlUtils

class RenderModulesTimelineReportStage(
    private val report: Report
) : Stage<String, String> {

    companion object {
        private const val MODULES_TIMELINE_METRIC_TEMPLATE_ID = "%modules-timeline-metric%"
        private const val MODULES_TIMELINE_METRIC_TEMPLATE_FILE_NAME = "modules-timeline-metric-template"
    }

    override suspend fun process(input: String): String {
        if (report.modulesTimelineReport.isNull()) {
            return input.replace(MODULES_TIMELINE_METRIC_TEMPLATE_ID, getEmptyRender())
        }

        return input.replace(MODULES_TIMELINE_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Modules Timeline is not available!")
    }

    fun getMetricRender(): String {
        var renderedTemplate = HtmlUtils.getTemplate(MODULES_TIMELINE_METRIC_TEMPLATE_FILE_NAME)

        var beginning = 0L
        var ending = 0L

        val result = buildString {
            append("[")
            appendLine()
            report.modulesTimelineReport.whenNotNull {
                beginning = start
                ending = end
                modules.forEachIndexed { index, module ->
                    append(
                        buildString {
                            append("{")
                            appendLine()
                            append("label: \"${module.path}\",")
                            appendLine()
                            append("times: [")
                            appendLine()
                            val color = getColor(index)
                            module.timelines.forEach { timeline ->
                                if (timeline.isCached) {
                                    append("{ \"color\": \"#999999\", \"starting_time\": ${
                                        timeline.start}, \"ending_time\": ${timeline.end} },")
                                } else {
                                    append("{ \"color\": \"$color\", \"starting_time\": ${
                                        timeline.start}, \"ending_time\": ${timeline.end} },")
                                }
                                appendLine()
                            }
                            append("]")
                            appendLine()
                            append("},")
                        }
                    )
                    appendLine()
                }
            }
            appendLine()
            append("]")
        }
        renderedTemplate = renderedTemplate
            .replace("%timelines%", result)
            .replace("%beginning%", beginning.toString())
            .replace("%ending%", ending.toString())
        return renderedTemplate
    }

    fun getColor(index: Int): String {
        val colors = listOf(
            "#3b76af",
            "#b3c6e5",
            "#ef8536",
            "#f5bd82",
            "#519d3e",
            "#a8dc93",
            "#c53a32",
            "#f19d99",
            "#8d6ab8",
            "#c2b1d2",
            "#84584e",
            "#be9e96",
            "#d57ebe",
            "#c2cd30"
        )
        return colors[index % colors.size]
    }

}
