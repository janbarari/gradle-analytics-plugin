package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ModulesMethodCountReport(
    val values: List<ModuleMethodCountReport>,
    val totalMethodCount: Int,
    val totalDiffRatio: Float? = null
)
