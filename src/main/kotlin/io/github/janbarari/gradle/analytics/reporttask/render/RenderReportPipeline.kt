package io.github.janbarari.gradle.analytics.reporttask.render

import io.github.janbarari.gradle.core.Pipeline
import io.github.janbarari.gradle.core.Stage

class RenderReportPipeline(firstStage: Stage<String, String>): Pipeline<String, String>(firstStage)
