/**
 * MIT License
 * Copyright (c) 2022 Mehdi Janbarari (@janbarari)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.metric.initialization.report

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ChartPoint
import io.github.janbarari.gradle.analytics.domain.model.report.InitializationReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.core.Triple
import io.github.janbarari.gradle.extension.*
import io.github.janbarari.gradle.utils.DateTimeUtils
import io.github.janbarari.gradle.utils.MathUtils

class CreateInitializationReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    companion object {
        private const val SKIP_THRESHOLD_IN_MS = 50L
        private const val CHART_MAX_COLUMNS = 12
    }

    override suspend fun process(report: Report): Report {

        val initializationChartPoints = metrics.filter { metric ->
            metric.initializationMetric.isNotNull() &&
                    metric.initializationMetric?.average.isNotNull() &&
                    metric.initializationMetric?.average?.isBiggerEquals(SKIP_THRESHOLD_IN_MS) ?: false
        }
        .mapToInitializationChartPoints()
        .minimize(CHART_MAX_COLUMNS)
        .whenEmpty {
            return report
        }
        initializationChartPoints

        val initializationReport = InitializationReport(
            values = initializationChartPoints.mapToCh,
            maxValue = initializationChartPoints.maxOf { it.value },
            minValue = initializationChartPoints.minOf { it.value }
        )

        return report.apply {
            this.initializationReport = initializationReport
        }
    }

    fun List<InitializationChartPoint>.minimize(
        targetSize: Int
    ): List<InitializationChartPoint> {
        return if (size > targetSize)
            calculatePointsMean(this).minimize(targetSize)
        else this
    }

    fun Collection<InitializationChartPoint>.mapToChartPoints(): Collection<ChartPoint> {
        return map {
            val period = if (it.finishedAt.isNull()) {
                DateTimeUtils.format(it.startedAt, "dd/MM")
            } else {
                DateTimeUtils.format(it.startedAt, "dd/MM") + "-" +
                        DateTimeUtils.format(ensureNotNull(it.finishedAt), "dd/MM")
            }
            ChartPoint(it.value, period)
        }
    }

    fun calculatePointsMean(values: List<InitializationChartPoint>): List<InitializationChartPoint> {

        if (values.isEmpty()) return values

        val mean = arrayListOf<InitializationChartPoint>()
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
                    InitializationChartPoint(
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

    class InitializationChartPoint(
        val value: Long,
        val startedAt: Long,
        val finishedAt: Long? = null
    ) : Triple<Long, Long, Long?>(value, startedAt, finishedAt)

    fun List<BuildMetric>.mapToInitializationChartPoints(): List<InitializationChartPoint> {
        return map {
            InitializationChartPoint(
                value = ensureNotNull(it.initializationMetric).average,
                startedAt = it.createdAt,
                finishedAt = null
            )
        }
    }
}
