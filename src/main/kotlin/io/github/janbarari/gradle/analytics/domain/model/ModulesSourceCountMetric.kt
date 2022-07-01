package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ModulesSourceCountMetric(
    val modules: List<ModuleSourceCount>
): java.io.Serializable

@ExcludeJacocoGenerated
data class ModuleSourceCount(
    val path: String,
    val value: Int,
    val coverage: Float?,
    val diffRatio: Float?
): java.io.Serializable
