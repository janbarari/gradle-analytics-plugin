package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ModuleCacheHitReport(
    val path: String,
    val hitRatio: Float,
    val diffRatio: Float? = null
): java.io.Serializable
