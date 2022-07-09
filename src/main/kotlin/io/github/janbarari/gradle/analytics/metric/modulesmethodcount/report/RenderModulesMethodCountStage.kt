package io.github.janbarari.gradle.analytics.metric.modulesmethodcount.report

import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.getTextResourceContent
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.whenNotNull

class RenderModulesMethodCountStage(
    private val report: Report
): Stage<String, String> {

    override suspend fun process(input: String): String {
        if (report.modulesMethodCountReport.isNull()) return input.replace(
            "%modules-method-count-metric%",
            "<p>Modules Method Count Metric is not available</p><div class\"space\"></div>"
        )

        val totalMethodCount = report.modulesMethodCountReport?.totalMethodCount ?: 0

        var totalDiffRatio = "<td>-</td>"
        report.modulesMethodCountReport.whenNotNull {
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
            report.modulesMethodCountReport?.values?.forEachIndexed { index, it ->
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

        val moduleLabels = report.modulesMethodCountReport?.values?.map { "\"${it.path}\"" }
        val moduleValues = report.modulesMethodCountReport?.values?.map { it.value }

        var template = getTextResourceContent("modules-method-count-metric-template.html")
        template = template.replace("%table-data%", tableData)
                .replace("%total-method-count%", totalMethodCount.toString())
                .replace("%total-diff-ratio%", totalDiffRatio)
                .replace("%module-labels%", moduleLabels.toString())
                .replace("%module-values%", moduleValues.toString())

        return input.replace("%modules-method-count-metric%", template)
    }

}
