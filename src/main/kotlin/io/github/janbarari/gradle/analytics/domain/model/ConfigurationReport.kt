package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.extension.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ConfigurationReport(
    val values: List<Long>,
    val maxValue: Long
): java.io.Serializable