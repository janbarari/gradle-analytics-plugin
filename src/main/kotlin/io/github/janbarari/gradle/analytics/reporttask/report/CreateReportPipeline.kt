package io.github.janbarari.gradle.analytics.reporttask.report

import io.github.janbarari.gradle.core.Pipeline
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.analytics.domain.model.Report

open class CreateReportPipeline(firstStage: Stage<Report, Report>) :
    Pipeline<Report, Report>(firstStage)
