package io.github.janbarari.gradle.analytics.domain.model.report

import com.squareup.moshi.JsonClass
import io.github.janbarari.gradle.ExcludeJacocoGenerated
import io.github.janbarari.gradle.analytics.domain.model.ChartPoint

@ExcludeJacocoGenerated
@JsonClass(generateAdapter = true)
data class SuccessBuildRateReport(
    val median: List<ChartPoint>
): java.io.Serializable
