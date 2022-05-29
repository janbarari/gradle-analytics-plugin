package io.github.janbarari.gradle.analytics.metric.initialization

import io.github.janbarari.gradle.analytics.core.Stage
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.AnalyticsReport
import io.github.janbarari.gradle.analytics.domain.model.InitializationReport

class InitializationMetricReportStage(
    val metrics: List<BuildMetric>
): Stage<AnalyticsReport, AnalyticsReport> {

    @Suppress("MagicNumber")
    override fun process(input: AnalyticsReport): AnalyticsReport {
        val values = listOf<Long>(
            1000,
            2000,
            3000,
            2000,
            3000,
            3000,
            5000,
            8000,
            7000,
            6000,
            6000,
            5000
        )
        val labels = listOf<String>(
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12"
        )
        val maxValue = values.maxOf { it }
        input.initializationReport = InitializationReport(
            values = values,
            maxValue = maxValue,
            labels = labels
        )
        return input
    }

}
