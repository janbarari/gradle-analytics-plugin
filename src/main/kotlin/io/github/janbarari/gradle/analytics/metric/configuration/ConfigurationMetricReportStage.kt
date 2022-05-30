package io.github.janbarari.gradle.analytics.metric.configuration

import io.github.janbarari.gradle.analytics.core.Stage
import io.github.janbarari.gradle.analytics.domain.model.AnalyticsReport
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ConfigurationReport

class ConfigurationMetricReportStage(
    val metrics: List<BuildMetric>
) : Stage<AnalyticsReport, AnalyticsReport> {

    override fun process(input: AnalyticsReport): AnalyticsReport {
        input.configurationReport = ConfigurationReport(listOf(1L, 2L), 2L)
        return input
    }

}
