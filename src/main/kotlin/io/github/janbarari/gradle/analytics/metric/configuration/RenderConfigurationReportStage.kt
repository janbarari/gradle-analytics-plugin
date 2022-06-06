package io.github.janbarari.gradle.analytics.metric.configuration

import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.analytics.metric.initialization.RenderInitializationReportStage
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.getTextResourceContent
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.removeLastChar
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.utils.MathUtils

class RenderConfigurationReportStage(
    private val report: Report
): Stage<String, String> {

    companion object {
        private const val CHART_EMPTY_POSITION_RATE = 30L
    }

    override fun process(input: String): String {
        if (report.configurationReport.isNull())
            return input.replace("%configuration-metric%",
            "<p>Configuration Median Chart is not available!</p><div class=\"space\"></div>")

        val values = report.configurationReport!!.values.map { it.value }.toIntList()

        val labels = StringBuilder()
        ensureNotNull(report.configurationReport).values.map { it.description }.whenEach {
            labels.append("\"$this\"").append(",")
        }
        // because the last item should not have ',' separator.
        labels.removeLastChar()

        val chartMaxValue = MathUtils.sumWithPercentage(
            ensureNotNull(report.configurationReport).maxValue,
            CHART_EMPTY_POSITION_RATE
        )
        val chartMinValue = MathUtils.deductWithPercentage(
            ensureNotNull(report.configurationReport).minValue,
            CHART_EMPTY_POSITION_RATE
        )

        var template = getTextResourceContent("configuration-metric-template.html")

        template = template
            .replace("%configuration-max-value%", chartMaxValue.toString())
            .replace("%configuration-min-value%", chartMinValue.toString())
            .replace("%configuration-median-values%", values.toString())
            .replace("%configuration-median-labels%", labels.toString())

        return input.replace("%configuration-metric%", template)
    }

}
