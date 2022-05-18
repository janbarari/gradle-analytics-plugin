package io.github.janbarari.gradle.analytics.domain.metric

import com.squareup.moshi.Json

data class BuildMetric(
    @Json(name = "initialization_metric")
    var initializationMetric: InitializationMetric? = null
): java.io.Serializable
