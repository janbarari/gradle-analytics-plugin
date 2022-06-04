package io.github.janbarari.gradle.analytics.metric.initialization.stage

import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.InitializationReport
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.utils.DatasetUtils

class CreateInitializationReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    @Suppress("MagicNumber")
    override fun process(input: Report): Report {
        if (metrics.isEmpty()) return input

        val values = DatasetUtils.resizeDataset(metrics.map { it.initializationMetric?.average ?: 0 }, 12)

        val maxValue = values.maxOf { it }

        val labels = values.map { it.toString() }

        input.initializationReport = InitializationReport(
            values = values,
            maxValue = maxValue,
            labels = labels
        )
        return input
    }

}
