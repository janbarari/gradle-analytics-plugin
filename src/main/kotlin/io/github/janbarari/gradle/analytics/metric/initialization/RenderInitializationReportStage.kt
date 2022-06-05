package io.github.janbarari.gradle.analytics.metric.initialization

import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.removeLastChar
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.utils.MathUtils

class RenderInitializationReportStage(
    private val report: Report
) : Stage<String, String> {

    companion object {
        private const val CHART_EMPTY_POSITION_RATE = 30L
    }

    override fun process(input: String): String {
        if (report.initializationReport.isNull()) return input

        val values = report.initializationReport!!.values.map { it.value }.toIntList()

        val labels = StringBuilder()
        ensureNotNull(report.initializationReport).values.map { it.description }.whenEach {
            labels.append("\"$this\"").append(",")
        }
        // because the last item should not have ',' separator.
        labels.removeLastChar()

        val chartMaxValue = MathUtils.sumWithPercentage(
            ensureNotNull(report.initializationReport).maxValue,
            CHART_EMPTY_POSITION_RATE
        )
        val chartMinValue = MathUtils.deductWithPercentage(
            ensureNotNull(report.initializationReport).minValue,
            CHART_EMPTY_POSITION_RATE
        )

        return input
            .replace("%initialization-max-value%", chartMaxValue.toString())
            .replace("%initialization-min-value%", chartMinValue.toString())
            .replace("%initialization-median-values%", values.toString())
            .replace("%initialization-median-labels%", labels.toString())
    }

}
