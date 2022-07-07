package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ModuleSourceCountReport(
    val path: String,
    val value: Int,
    val coverage: Float,
    val diffRatio: Float? = null
): java.io.Serializable
