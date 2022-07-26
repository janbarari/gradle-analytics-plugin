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

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.report.ExecutionReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isBiggerEquals
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.mapToChartPoints
import io.github.janbarari.gradle.extension.mapToExecutionTimespanChartPoints
import io.github.janbarari.gradle.extension.maxValue
import io.github.janbarari.gradle.extension.minValue
import io.github.janbarari.gradle.extension.minimize
import io.github.janbarari.gradle.extension.whenEmpty

class CreateExecutionReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    companion object {
        private const val SKIP_METRIC_THRESHOLD = 50L
        private const val CHART_MAX_COLUMNS = 12
    }

    override suspend fun process(report: Report): Report {
        val chartPoints = metrics.filter { metric ->
            metric.executionMetric.isNotNull() &&
                    metric.executionMetric?.average.isNotNull() &&
                    metric.executionMetric?.average?.isBiggerEquals(SKIP_METRIC_THRESHOLD) ?: false
        }.mapToExecutionTimespanChartPoints()
            .minimize(CHART_MAX_COLUMNS)
            .mapToChartPoints()
            .whenEmpty {
                return report
            }

        return report.apply {
            executionReport = ExecutionReport(
                values = chartPoints,
                maxValue = chartPoints.maxValue(),
                minValue = chartPoints.minValue()
            )
        }
    }
}
