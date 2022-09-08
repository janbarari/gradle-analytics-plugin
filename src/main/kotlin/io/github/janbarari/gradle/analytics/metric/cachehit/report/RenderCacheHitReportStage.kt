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
package io.github.janbarari.gradle.analytics.metric.cachehit.report

import io.github.janbarari.gradle.analytics.domain.model.report.ModuleCacheHit
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.toArrayString
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.HtmlUtils

class RenderCacheHitReportStage(
    private val report: Report
) : Stage<String, String> {

    companion object {
        private const val CACHE_HIT_METRIC_TEMPLATE_ID = "%cache-hit-metric%"
        private const val CACHE_HIT_METRIC_TEMPLATE_FILE_NAME = "cache-hit-metric-template"
    }

    override suspend fun process(input: String): String {
        if (report.cacheHitReport.isNull())
            return input.replace(CACHE_HIT_METRIC_TEMPLATE_ID, getEmptyRender())

        return input.replace(CACHE_HIT_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Cache Hit is not available!")
    }

    @Suppress("LongMethod")
    fun getMetricRender(): String {
        val overallChartValues = report.cacheHitReport!!
            .overallMeanValues
            .map { it.value }
            .toIntList()
            .toString()

        val overallChartLabels = report.cacheHitReport!!
            .overallMeanValues
            .map { it.description }
            .toArrayString()

        val tableData = buildString {
            report.cacheHitReport?.modules?.forEachIndexed { index, it ->
                var diffRatioRender = "<td>-</td>"
                it.diffRate.whenNotNull {
                    diffRatioRender = if (this > 0) {
                        "<td class=\"green\">+${this}%</td>"
                    } else if (this < 0) {
                        "<td class=\"red\">${this}%</td>"
                    } else {
                        "<td>Equals</td>"
                    }
                }
                append(
                    """
                    <tr>
                        <td>${index + 1}</td>
                        <td>${it.path}</td>
                        <td>${it.rate}%</td>
                        $diffRatioRender
                    </tr>
                """.trimIndent()
                )
            }
        }

        val overallCacheHit = report.cacheHitReport!!.overallRate.toString() + "%"

        var overallDiffRatioRender = "<td>-</td>"
        report.cacheHitReport!!.overallDiffRate.whenNotNull {
            overallDiffRatioRender = if (this > 0) {
                "<td class=\"green\">+${this}%</td>"
            } else if (this < 0) {
                "<td class=\"red\">${this}%</td>"
            } else {
                "<td>Equals</td>"
            }
        }

        val bestModulePath = getBestModulePath(report.cacheHitReport!!.modules)
        val worstModulePath = getWorstModulePath(report.cacheHitReport!!.modules)

        val modules = report.cacheHitReport!!.modules

        var bestChartValues = "[]"
        var worstChartValues = "[]"
        var bwLabels = "[]"

        if (modules.isNotEmpty()) {
            bestChartValues = modules
                .first { it.path == bestModulePath }
                .meanValues
                .map { it.value }
                .toIntList()
                .toString()

            worstChartValues = modules
                .first { it.path == worstModulePath }
                .meanValues
                .map { it.value }
                .toIntList()
                .toString()

            bwLabels = modules
                .first { it.path == worstModulePath }
                .meanValues
                .map { it.description }
                .toArrayString()
        }

        var template = HtmlUtils.getTemplate(CACHE_HIT_METRIC_TEMPLATE_FILE_NAME)
        template = template
            .replace("%chart-values%", overallChartValues)
            .replace("%chart-labels%", overallChartLabels)
            .replace("%table-data%", tableData)
            .replace("%overall-cache-hit%", overallCacheHit)
            .replace("%overall-diff-rate%", overallDiffRatioRender)
            .replace("%best-values%", bestChartValues)
            .replace("%worst-values%", worstChartValues)
            .replace("%bw-labels%", bwLabels.toString())
            .replace("%worst-module-name%", "\"$worstModulePath\"")
            .replace("%best-module-name%", "\"$bestModulePath\"")

        return template
    }

    fun getBestModulePath(modules: List<ModuleCacheHit>): String? {
        if (modules.isEmpty()) return null
        return modules.sortedByDescending { module ->
            module.meanValues.sumOf { it.value }
        }.first().path
    }

    fun getWorstModulePath(modules: List<ModuleCacheHit>): String? {
        if (modules.isEmpty()) return null
        return modules.sortedByDescending { module ->
            module.meanValues.sumOf { it.value }
        }.last().path
    }

}
