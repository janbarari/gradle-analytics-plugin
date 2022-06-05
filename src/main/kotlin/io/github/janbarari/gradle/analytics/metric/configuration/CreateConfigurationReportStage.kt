package io.github.janbarari.gradle.analytics.metric.configuration

import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ChartPoint
import io.github.janbarari.gradle.analytics.domain.model.ConfigurationReport
import io.github.janbarari.gradle.core.Triple
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.isBiggerEquals
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.utils.DateTimeUtils
import io.github.janbarari.gradle.utils.MathUtils

class CreateConfigurationReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    companion object {
        private const val SKIP_METRIC_THRESHOLD = 50L
        private const val CHART_MAX_COLUMNS = 12
    }

    override fun process(input: Report): Report {

        val configurationChartPoints = metrics.filter { it.configurationMetric.isNotNull() }
            .filter { ensureNotNull(it.configurationMetric).average.isNotNull() }
            .filter { ensureNotNull(it.configurationMetric).average.isBiggerEquals(SKIP_METRIC_THRESHOLD) }
            .map {
                ConfigurationChartPoint(
                    value = ensureNotNull(it.configurationMetric).average,
                    startedAt = it.createdAt,
                    finishedAt = null
                )
            }

        val configurationMetricsMean = resizeConfigurationChartPoints(configurationChartPoints, CHART_MAX_COLUMNS)

        if (configurationMetricsMean.isEmpty()) return input

        val configurationReport = ConfigurationReport(
            values = configurationChartPoints.map {
                val period = if (it.finishedAt.isNull()) {
                    DateTimeUtils.format(it.startedAt, "dd/MM")
                } else {
                    DateTimeUtils.format(it.startedAt, "dd/MM") + "-" +
                            DateTimeUtils.format(ensureNotNull(it.finishedAt), "dd/MM")
                }
                ChartPoint(it.value, period)
            },
            maxValue = configurationMetricsMean.maxOf { it.value },
            minValue = configurationMetricsMean.minOf { it.value }
        )

        input.configurationReport = configurationReport

        return input
    }

    fun resizeConfigurationChartPoints(
        input: List<ConfigurationChartPoint>, targetSize: Int
    ): List<ConfigurationChartPoint> {
        return if (input.size > targetSize)
            resizeConfigurationChartPoints(calculatePointsMean(input), targetSize)
        else input
    }

    fun calculatePointsMean(values: List<ConfigurationChartPoint>): List<ConfigurationChartPoint> {

        if (values.isEmpty()) return values

        val mean = arrayListOf<ConfigurationChartPoint>()
        val size = values.size
        var nextIndex = 0

        for (i in values.indices) {
            if (i < nextIndex) continue

            if (i + 1 >= size) {
                mean.add(values[i])
            } else {

                var finishedAt = values[i + 1].finishedAt
                if (finishedAt.isNull()) finishedAt = values[i + 1].startedAt

                mean.add(
                    ConfigurationChartPoint(
                        value = MathUtils.longMean(values[i].value, values[i + 1].value),
                        startedAt = values[i].startedAt,
                        finishedAt = finishedAt
                    )
                )

                nextIndex = i + 2
            }
        }

        return mean
    }

    class ConfigurationChartPoint(
        val value: Long,
        val startedAt: Long,
        val finishedAt: Long? = null
    ) : Triple<Long, Long, Long?>(value, startedAt, finishedAt)


}
