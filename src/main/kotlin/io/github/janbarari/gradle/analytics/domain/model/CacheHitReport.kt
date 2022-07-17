package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class CacheHitReport(
    val modules: List<ModuleCacheHitReport>,
    val hitRatio: Float,
    val diffRatio: Float? = null
)
