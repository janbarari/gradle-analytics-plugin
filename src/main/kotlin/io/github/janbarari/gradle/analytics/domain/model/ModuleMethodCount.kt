package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ModuleMethodCount(
    val path: String,
    val value: Int
): java.io.Serializable
