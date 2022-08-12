package io.github.janbarari.gradle.analytics.domain.model.metric

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
@JsonClass(generateAdapter = true)
data class SuccessBuildRateMetric(
    @Json(name = "median_rate")
    var medianRate: Float = 0f,
    @Json(name = "mean_rate")
    var meanRate: Float = 0f
) : java.io.Serializable
