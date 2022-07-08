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
package io.github.janbarari.gradle.analytics.metric.execution.report

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ChartPoint
import io.github.janbarari.gradle.analytics.domain.model.ExecutionReport
import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.core.Triple
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.isBiggerEquals
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.utils.DateTimeUtils
import io.github.janbarari.gradle.utils.MathUtils

class CreateExecutionReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    companion object {
        private const val SKIP_METRIC_THRESHOLD = 50L
        private const val CHART_MAX_COLUMNS = 12
    }

    @Suppress("MagicNumber")
    override suspend fun process(input: Report): Report {
        val executionChartPoints = metrics.filter { it.executionMetric.isNotNull() }
            .filter { ensureNotNull(it.executionMetric).average.isNotNull() }
            .filter { ensureNotNull(it.executionMetric).average.isBiggerEquals(SKIP_METRIC_THRESHOLD) }
            .map {
                ExecutionChartPoint(
                    value = (ensureNotNull(it.executionMetric).average / 1000),
                    startedAt = it.createdAt,
                    finishedAt = null
                )
            }

        val executionMetricsMean = resizeExecutionChartPoints(
            executionChartPoints,
            CHART_MAX_COLUMNS
        )

        if (executionMetricsMean.isEmpty()) return input

        val executionReport = ExecutionReport(
            values = executionChartPoints.map {
                val period = if (it.finishedAt.isNull()) {
                    DateTimeUtils.format(it.startedAt, "dd/MM")
                } else {
                    DateTimeUtils.format(it.startedAt, "dd/MM") + "-" +
                            DateTimeUtils.format(ensureNotNull(it.finishedAt), "dd/MM")
                }
                ChartPoint(it.value, period)
            },
            maxValue = executionMetricsMean.maxOf { it.value },
            minValue = executionMetricsMean.minOf { it.value }
        )

        input.executionReport = executionReport

        return input
    }

    fun resizeExecutionChartPoints(
        input: List<ExecutionChartPoint>, targetSize: Int
    ): List<ExecutionChartPoint> {
        return if (input.size > targetSize)
            resizeExecutionChartPoints(calculatePointsMean(input), targetSize)
        else input
    }

    fun calculatePointsMean(values: List<ExecutionChartPoint>): List<ExecutionChartPoint> {

        if (values.isEmpty()) return values

        val mean = arrayListOf<ExecutionChartPoint>()
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
                    ExecutionChartPoint(
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

    class ExecutionChartPoint(
        val value: Long,
        val startedAt: Long,
        val finishedAt: Long? = null
    ) : Triple<Long, Long, Long?>(value, startedAt, finishedAt)

}
