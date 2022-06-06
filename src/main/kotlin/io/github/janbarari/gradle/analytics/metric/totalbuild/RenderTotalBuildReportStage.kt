package io.github.janbarari.gradle.analytics.metric.totalbuild

import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.getTextResourceContent
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.removeLastChar
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.utils.MathUtils

class RenderTotalBuildReportStage(
    private val report: Report
): Stage<String, String> {

    companion object {
        private const val CHART_EMPTY_POSITION_RATE = 30L
    }

    override fun process(input: String): String {
        if (report.totalBuildReport.isNull())
            return input.replace("%totalbuild-metric%",
                "<p>Total Build Median Chart is not available!</p><div class=\"space\"></div>")

        val values = ensureNotNull(report.totalBuildReport).values.map { it.value }.toIntList()

        val labels = StringBuilder()
        ensureNotNull(report.totalBuildReport).values.map { it.description }.whenEach {
            labels.append("\"$this\"").append(",")
        }
        // because the last item should not have ',' separator.
        labels.removeLastChar()

        val chartMaxValue = MathUtils.sumWithPercentage(
            ensureNotNull(report.totalBuildReport).maxValue,
            CHART_EMPTY_POSITION_RATE
        )
        val chartMinValue = MathUtils.deductWithPercentage(
            ensureNotNull(report.totalBuildReport).minValue,
            CHART_EMPTY_POSITION_RATE
        )

        var template = getTextResourceContent("totalbuild-metric-template.html")

        template = template
            .replace("%totalbuild-max-value%", chartMaxValue.toString())
            .replace("%totalbuild-min-value%", chartMinValue.toString())
            .replace("%totalbuild-median-values%", values.toString())
            .replace("%totalbuild-median-labels%", labels.toString())

        return input.replace("%totalbuild-metric%", template)
    }

}
