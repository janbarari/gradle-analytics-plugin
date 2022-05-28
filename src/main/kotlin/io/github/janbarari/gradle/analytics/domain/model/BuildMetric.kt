package io.github.janbarari.gradle.analytics.domain.model

import com.squareup.moshi.Json

data class BuildMetric(
    @Json(name = "branch")
    var branch: String,
    @Json(name = "requestedTasks")
    var requestedTasks: List<String>,
    @Json(name = "initialization_metric")
    var initializationMetric: InitializationMetric? = null
): java.io.Serializable
