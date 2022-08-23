package io.github.janbarari.gradle.analytics.domain.model.metric

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
@JsonClass(generateAdapter = true)
data class ModuleExecutionProcess(
    @Json(name = "path")
    val path: String,
    @Json(name = "average_duration")
    val duration: Long,
    @Json(name = "average_parallel_duration")
    val parallelDuration: Long,
    @Json(name = "parallel_rate")
    val parallelRate: Float,
    @Json(name = "coverage")
    val coverage: Float
) : java.io.Serializable
