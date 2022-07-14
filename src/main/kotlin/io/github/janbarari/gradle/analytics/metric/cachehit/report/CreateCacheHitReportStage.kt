package io.github.janbarari.gradle.analytics.metric.cachehit.report

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage

class CreateCacheHitReportStage(
    private val metrics: List<BuildMetric>
): Stage<Report, Report> {

    override suspend fun process(input: Report): Report {
        return input
    }

}
