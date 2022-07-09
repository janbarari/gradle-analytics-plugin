package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ModulesMethodCountMetric(
    val modules: List<ModuleMethodCount>
): java.io.Serializable

