package io.github.janbarari.gradle.analytics.metric.execution

import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.analytics.metric.configuration.RenderConfigurationReportStage
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.getTextResourceContent
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.removeLastChar
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.utils.MathUtils

class RenderExecutionReportStage(
    private val report: Report
): Stage<String, String> {

    companion object {
        private const val CHART_EMPTY_POSITION_RATE = 30L
    }

    override fun process(input: String): String {
        if (report.executionReport.isNull())
            return input.replace("%execution-metric%",
                "<p>Execution Median Chart is not available!</p><div class=\"space\"></div>")

        val values = ensureNotNull(report.executionReport).values.map { it.value }.toIntList()

        val labels = StringBuilder()
        ensureNotNull(report.executionReport).values.map { it.description }.whenEach {
            labels.append("\"$this\"").append(",")
        }
        // because the last item should not have ',' separator.
        labels.removeLastChar()

        val chartMaxValue = MathUtils.sumWithPercentage(
            ensureNotNull(report.executionReport).maxValue,
            CHART_EMPTY_POSITION_RATE
        )
        val chartMinValue = MathUtils.deductWithPercentage(
            ensureNotNull(report.executionReport).minValue,
            CHART_EMPTY_POSITION_RATE
        )

        var template = getTextResourceContent("execution-metric-template.html")

        template = template
            .replace("%execution-max-value%", chartMaxValue.toString())
            .replace("%execution-min-value%", chartMinValue.toString())
            .replace("%execution-median-values%", values.toString())
            .replace("%execution-median-labels%", labels.toString())

        return input.replace("%execution-metric%", template)
    }

}
