package io.github.janbarari.gradle.analytics.metric.modulesourcecount

import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.getTextResourceContent
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.whenNotNull

class RenderModulesSourceCountStage(
    private val report: Report
): Stage<String, String> {

    override suspend fun process(input: String): String {
        if (report.modulesSourceCountReport.isNull())
            return input.replace("%modules-source-count-metric%",
                "<p>Modules Source Count Metric is not available!</p><div class=\"space\"></div>")

        val totalSourceCount = report.modulesSourceCountReport?.totalSourceCount ?: 0

        var totalDiffRatio = "-"
        report.modulesSourceCountReport.whenNotNull {
            totalDiffRatio = if (this.totalDiffRatio > 0) {
                "${this.totalDiffRatio}%+"
            } else {
                "${this.totalDiffRatio}%-"
            }
        }

        var template = getTextResourceContent("modules-source-count-metric-template.html")
        template = template
            .replace("%total-source-count%", totalSourceCount.toString())
            .replace("%total-diff-ratio%", totalDiffRatio)

        return input.replace("%modules-source-count-metric%", template)
    }

}
