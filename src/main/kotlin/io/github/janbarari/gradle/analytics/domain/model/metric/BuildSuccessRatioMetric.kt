package io.github.janbarari.gradle.analytics.domain.model.metric

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
@JsonClass(generateAdapter = true)
data class BuildSuccessRatioMetric(
    @Json(name = "is_successful")
    val isSuccessful: Boolean,
    @Json(name = "ratio")
    var ratio: Float = 0f
) : java.io.Serializable
