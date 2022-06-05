package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ChartPoint(
    val value: Long,
    val description: String
): java.io.Serializable
