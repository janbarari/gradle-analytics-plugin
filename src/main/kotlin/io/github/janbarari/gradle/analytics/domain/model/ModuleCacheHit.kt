package io.github.janbarari.gradle.analytics.domain.model

import com.squareup.moshi.Json
import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ModuleCacheHit(
    @Json(name = "path")
    val path: String,
    @Json(name = "value")
    val value: Float
): java.io.Serializable
