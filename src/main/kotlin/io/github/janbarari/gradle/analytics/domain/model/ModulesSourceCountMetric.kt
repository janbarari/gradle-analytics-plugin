package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ModulesSourceCountMetric(
    val modules: List<ModuleProperties>
): java.io.Serializable

@ExcludeJacocoGenerated
data class ModuleProperties(
    val modulePath: String,
    val totalSourceFiles: Int
): java.io.Serializable
