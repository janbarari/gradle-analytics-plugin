package io.github.janbarari.gradle.analytics.metric.initialization

import io.github.janbarari.gradle.analytics.core.Stage
import io.github.janbarari.gradle.analytics.domain.model.AnalyticsReport
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.utils.MathUtils

class InitializationMetricRenderStage(
    private val analyticsReport: AnalyticsReport
): Stage<String, String> {

    @Suppress("MagicNumber")
    override fun process(input: String): String {
        if (analyticsReport.initializationReport.isNull()) return input
        val chartMaxValue = MathUtils.sumPercentage(analyticsReport.initializationReport!!.maxValue, 35)

        val values = analyticsReport.initializationReport!!.values
        val labels = analyticsReport.initializationReport!!.labels

        println("InitializationMetricRenderStage chartMaxValue=$chartMaxValue")
        println("InitializationMetricRenderStage initialization-median-values=$values")
        println("InitializationMetricRenderStage initialization-median-labels=$labels")

        return input.replace("%initialization-max-value%", chartMaxValue.toString())
            .replace("%initialization-median-values%", values.toString())
            .replace("%initialization-median-labels%", labels.toString())
    }

}
