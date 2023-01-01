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
package io.github.janbarari.gradle.analytics.metric.noncacheabletasks.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.toArrayRender
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.utils.HtmlUtils

class RenderNonCacheableTasksReportStage(
    private val tower: Tower,
    private val report: Report
): Stage<String, String> {

    companion object {
        private const val NON_CACHEABLE_TASKS_METRIC_TEMPLATE_ID = "%non-cacheable-tasks-metric%"
        private const val NON_CACHEABLE_TASKS_METRIC_TEMPLATE_FILENAME = "non-cacheable-tasks-metric-template"
        private val clazz = RenderNonCacheableTasksReportStage::class.java
    }

    override suspend fun process(input: String): String {
        tower.i(clazz, "process()")
        if (report.nonCacheableTasksReport.isNull())
            return input.replace(NON_CACHEABLE_TASKS_METRIC_TEMPLATE_ID, getEmptyRender())

        return input.replace(NON_CACHEABLE_TASKS_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Non-cacheable Tasks is not available!")
    }

    fun getMetricRender(): String {
        var renderedTemplate = HtmlUtils.getTemplate(NON_CACHEABLE_TASKS_METRIC_TEMPLATE_FILENAME)
        report.nonCacheableTasksReport.whenNotNull {
            val labels = mutableListOf<String>()
            val colors = mutableListOf<String>()
            val dataset = mutableListOf<Long>()

            tasks.sortedByDescending { it.avgExecutionDurationInMillis }
                .whenEach {
                    labels.add(path)
                    colors.add(getRandomColor())
                    dataset.add(avgExecutionDurationInMillis)
                }

            val chartHeight = dataset.size * 36

            renderedTemplate = renderedTemplate
                .replace("%labels%", labels.toArrayRender())
                .replace("%colors%", colors.toArrayRender())
                .replace("%dataset%", dataset.toString())
                .replace("%chart-height%", "${chartHeight}px")
        }
        return renderedTemplate
    }

    fun getRandomColor(): String {
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
        return colors[colors.indices.random() % colors.size]
    }

}
