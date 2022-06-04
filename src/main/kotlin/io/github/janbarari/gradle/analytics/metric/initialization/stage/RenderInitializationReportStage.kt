package io.github.janbarari.gradle.analytics.metric.initialization.stage

import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.utils.MathUtils

class RenderInitializationReportStage(
    private val report: Report
) : Stage<String, String> {

    @Suppress("MagicNumber")
    override fun process(input: String): String {
        if (report.initializationReport.isNull()) return input
        val chartMaxValue = MathUtils.sumPercentage(report.initializationReport!!.maxValue, 35)

        val values = report.initializationReport!!.values
        val labels = report.initializationReport!!.labels

        return input.replace("%initialization-max-value%", chartMaxValue.toString())
            .replace("%initialization-median-values%", values.toString())
            .replace("%initialization-median-labels%", labels.toString())
    }

}
