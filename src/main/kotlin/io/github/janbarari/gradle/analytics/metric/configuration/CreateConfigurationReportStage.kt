package io.github.janbarari.gradle.analytics.metric.configuration

import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ConfigurationReport

class CreateConfigurationReportStage(
    val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    override fun process(input: Report): Report {
        input.configurationReport = ConfigurationReport(listOf(1L, 2L), 2L)
        return input
    }

}
