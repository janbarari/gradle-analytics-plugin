package io.github.janbarari.gradle.analytics.domain.usecase

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.core.Pipeline
import io.github.janbarari.gradle.core.Stage

class UpdateMetricPipeline(firstStage: Stage<BuildMetric, BuildMetric>): Pipeline<BuildMetric, BuildMetric>(firstStage)