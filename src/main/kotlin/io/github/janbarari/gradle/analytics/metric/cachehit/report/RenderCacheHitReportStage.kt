package io.github.janbarari.gradle.analytics.metric.cachehit.report

import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage

class RenderCacheHitReportStage(
    private val report: Report
): Stage<String, String> {

    override suspend fun process(input: String): String {
        return ""
    }

}
