package io.github.janbarari.gradle.analytics.reporttask.analytics

import io.github.janbarari.gradle.core.Pipeline
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.analytics.domain.model.AnalyticsReport

open class AnalyticsReportPipeline(firstStage: Stage<AnalyticsReport, AnalyticsReport>) :
    Pipeline<AnalyticsReport, AnalyticsReport>(firstStage)
