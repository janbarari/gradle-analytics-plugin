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
package io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.SuspendStage
import io.github.janbarari.gradle.extension.isBiggerThanZero
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.isZero
import io.github.janbarari.gradle.extension.mapToChartPoints
import io.github.janbarari.gradle.extension.millisToSeconds
import io.github.janbarari.gradle.extension.minimize
import io.github.janbarari.gradle.extension.toArrayRender
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.utils.HtmlUtils
import io.github.janbarari.gradle.utils.MathUtils

/**
 * Generates html render for [io.github.janbarari.gradle.analytics.domain.model.report.ModulesExecutionProcessReport].
 */
class RenderModulesExecutionProcessReportStage(
    private val tower: Tower,
    private val report: Report
) : SuspendStage<String, String> {

    companion object {
        private const val CHART_MAX_COLUMNS = 12
        private const val CHART_SUGGESTED_MIN_MAX_PERCENTAGE = 30
        private const val MODULES_EXECUTION_PROCESS_METRIC_TEMPLATE_ID = "%modules-execution-process-metric%"
        private const val MODULES_EXECUTION_PROCESS_METRIC_FILE_NAME = "modules-execution-process-metric-template"
        private val clazz = RenderModulesExecutionProcessReportStage::class.java
    }

    override suspend fun process(input: String): String {
        tower.i(clazz, "process()")
        if (report.modulesExecutionProcessReport.isNull()) {
            return input.replace(MODULES_EXECUTION_PROCESS_METRIC_TEMPLATE_ID, getEmptyRender())
        }

        return input.replace(MODULES_EXECUTION_PROCESS_METRIC_TEMPLATE_ID, getMetricRender())
    }

    fun getMetricRender(): String {
        var renderedTemplate = HtmlUtils.getTemplate(MODULES_EXECUTION_PROCESS_METRIC_FILE_NAME)
        report.modulesExecutionProcessReport.whenNotNull {
            val min = (modules.minOfOrNull { it.avgMedianExecInMillis } ?: 0L).millisToSeconds()
            val max = (modules.maxOfOrNull { it.avgMedianExecInMillis } ?: 0L).millisToSeconds()

            val chartSuggestedMinValue = MathUtils.deductWithPercentage(min, CHART_SUGGESTED_MIN_MAX_PERCENTAGE)
            val chartSuggestedMaxValue = MathUtils.sumWithPercentage(max, CHART_SUGGESTED_MIN_MAX_PERCENTAGE)

            val chartLabels: String = modules.firstOrNull()
                ?.avgMedianExecs
                ?.minimize(CHART_MAX_COLUMNS)
                ?.mapToChartPoints()
                ?.map { it.description }
                ?.toArrayRender()
                ?: "[]"

            val chartDatasets = buildString {
                modules.whenEach {
                    append("{")
                    append("label: \"$path\",")
                    append("fill: false,")
                    append("borderColor: getColor(),")
                    append("backgroundColor: shadeColor(getColor(), 25),")
                    append("pointRadius: 0,")
                    append("data: ${avgMedianExecs.map { it.value.millisToSeconds() }.toIntList()},")
                    append("cubicInterpolationMode: 'monotone',")
                    append("tension: 0.4,")
                    append("hidden: false")
                    append("}")
                    append(",")
                }
            }

            val tableData = buildString {
                modules
                    .sortedByDescending {
                        it.avgMedianExecInMillis
                    }
                    .forEachIndexed { i, module ->
                    append("<tr>")
                    append("<th>${i+1}</th>")
                    append("<th>${module.path}</th>")
                    append("<th>${module.avgMedianExecInMillis.millisToSeconds()}s</th>")
                    append("<th>${module.avgMedianParallelExecInMillis.millisToSeconds()}s</th>")
                    append("<th>${module.avgMedianParallelRate}%</th>")
                    append("<th>${module.avgMedianCoverageRate}%</th>")

                    if (module.diffRate.isNull())
                        append("<th>Unknown</th>")
                    else if (module.diffRate!!.isZero())
                        append("<th>Equals</th>")
                    else if (module.diffRate.isBiggerThanZero())
                        append("<th class=\"red\">+${module.diffRate}%</th>")
                    else
                        append("<th class=\"green\">${module.diffRate}%</th>")

                    append("</tr>")
                }
            }

            var chartHeight = 400
            // 25 px per each module
            if (modules.size * 25 > 400) {
                chartHeight = modules.size * 25
            }

            renderedTemplate = renderedTemplate
                .replace("%suggested-min-value%", chartSuggestedMinValue.toString())
                .replace("%suggested-max-value%", chartSuggestedMaxValue.toString())
                .replace("%chart-labels%", chartLabels)
                .replace("%chart-datasets%", chartDatasets)
                .replace("%table-data%", tableData)
                .replace("%chart-height%", "$chartHeight")
        }
        return renderedTemplate
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Modules Execution Process is not available!")
    }

}
