package io.github.janbarari.gradle.analytics.domain.model

import com.squareup.moshi.Json
import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ConfigurationMetric(
    @Json(name = "average")
    var average: Long = 0L
): java.io.Serializable
