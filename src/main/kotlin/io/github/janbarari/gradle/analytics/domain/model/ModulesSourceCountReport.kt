package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ModulesSourceCountReport(
    val values: List<ModuleSourceCount>,
    val totalSourceCount: Int,
    val totalDiffRatio: Float
)
