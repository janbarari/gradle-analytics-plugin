package io.github.janbarari.gradle.analytics.domain.metric

import com.squareup.moshi.Json

data class InitializationMetric(
    @Json(name = "average")
    val average: Long = 0L
): java.io.Serializable
