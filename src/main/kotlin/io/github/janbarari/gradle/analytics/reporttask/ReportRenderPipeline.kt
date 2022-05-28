package io.github.janbarari.gradle.analytics.reporttask

import io.github.janbarari.gradle.analytics.core.Pipeline
import io.github.janbarari.gradle.analytics.core.Stage

class ReportRenderPipeline(firstStage: Stage<String, String>): Pipeline<String, String>(firstStage)
