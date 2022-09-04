package io.github.janbarari.gradle.analytics.domain.model.report

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
@JsonClass(generateAdapter = true)
data class ModuleBuildHeatmap(
    @Json(name = "path")
    val path: String,
    @Json(name = "dependant_modules_count")
    val dependantModulesCount: Int,
    @Json(name = "avg_median_cache_hit")
    val avgMedianCacheHit: Long,
    @Json(name = "total_build_count")
    val totalBuildCount: Int
): java.io.Serializable
