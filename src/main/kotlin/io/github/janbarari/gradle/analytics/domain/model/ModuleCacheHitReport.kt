package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ModuleCacheHitReport(
    val path: String,
    val hitRatio: Long,
    val diffRatio: Float? = null,
    val values: List<ChartPoint>
): java.io.Serializable
