package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class ExecutionReport(
    val values: List<ChartPoint>,
    val maxValue: Long,
    val minValue: Long
): java.io.Serializable
