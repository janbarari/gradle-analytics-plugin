package io.github.janbarari.gradle.analytics.metric.initialization

import io.github.janbarari.gradle.analytics.core.Stage
import io.github.janbarari.gradle.analytics.domain.model.AnalyticsReport

class InitializationMetricRenderStage(
    private val analyticsReport: AnalyticsReport
): Stage<String, String> {

    @Suppress("MagicNumber")
    override fun process(input: String): String {
        val chartMaxValue: Long = analyticsReport.initializationReport!!.maxValue +
                (analyticsReport.initializationReport!!.maxValue * 35) / 100
        return input.replace("%initialization-max-value%", chartMaxValue.toString())
            .replace("%initialization-median-values%", analyticsReport.initializationReport!!.values.toString())
            .replace("%initialization-median-labels%", analyticsReport.initializationReport!!.labels.toString())
    }

}
