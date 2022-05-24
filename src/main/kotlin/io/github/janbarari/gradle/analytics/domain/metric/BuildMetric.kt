package io.github.janbarari.gradle.analytics.domain.metric

import com.squareup.moshi.Json

data class BuildMetric(
    @Json(name = "branch")
    var branch: String? = null,
    @Json(name = "requestedTasks")
    var requestedTasks: List<String>? = null,
    @Json(name = "initialization_metric")
    var initializationMetric: InitializationMetric? = null
): java.io.Serializable
