package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class InitializationReport(
    val values: List<Long>,
    val labels: List<String>,
    val maxValue: Long
): java.io.Serializable
