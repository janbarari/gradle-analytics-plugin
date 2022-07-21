package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class CacheHitReport(
    val modules: List<ModuleCacheHitReport>,
    val overallValues: List<ChartPoint>,
    val overallHit: Long,
    val overallDiffRatio: Float? = null
)
