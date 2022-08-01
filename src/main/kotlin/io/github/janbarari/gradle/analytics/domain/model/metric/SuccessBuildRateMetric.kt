package io.github.janbarari.gradle.analytics.domain.model.metric

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
@JsonClass(generateAdapter = true)
data class SuccessBuildRateMetric(
    @Json(name = "ratio")
    var ratio: Float = 0f
) : java.io.Serializable
