package io.github.janbarari.gradle.analytics.scanner.execution

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.core.Pipeline
import io.github.janbarari.gradle.core.Stage

class CreateMetricPipeline(firstStage: Stage<BuildMetric, BuildMetric>): Pipeline<BuildMetric, BuildMetric>(firstStage)
