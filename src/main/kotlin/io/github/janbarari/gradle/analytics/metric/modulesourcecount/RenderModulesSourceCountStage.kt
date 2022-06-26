package io.github.janbarari.gradle.analytics.metric.modulesourcecount

import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage

class RenderModulesSourceCountStage(
    private val report: Report
): Stage<String, String> {

    override suspend fun process(input: String): String {
        return input
    }

}
