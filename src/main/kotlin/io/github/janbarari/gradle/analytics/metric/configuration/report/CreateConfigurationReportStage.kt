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
package io.github.janbarari.gradle.analytics.metric.configuration.report

import io.github.janbarari.gradle.analytics.CHART_MAX_COLUMNS
import io.github.janbarari.gradle.analytics.SKIP_METRIC_THRESHOLD
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.ChartPoint
import io.github.janbarari.gradle.analytics.domain.model.ConfigurationReport
import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.analytics.domain.model.TimespanChartPoint
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.isBiggerEquals
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.whenEmpty
import io.github.janbarari.gradle.utils.DatasetUtils

class CreateConfigurationReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE", "ReturnCount", "FunctionOnlyReturningConstant")
    override suspend fun process(report: Report): Report {
        val chartPoints = metrics.filter { it.configurationMetric.isNotNull() }
            .filter { ensureNotNull(it.configurationMetric).average.isNotNull() }
            .filter { ensureNotNull(it.configurationMetric).average.isBiggerEquals(SKIP_METRIC_THRESHOLD) }
            .map {
                TimespanChartPoint(
                    value = ensureNotNull(it.configurationMetric).average,
                    from = it.createdAt,
                    to = null
                )
            }
            .whenEmpty {
                return report
            }

        val dataset = DatasetUtils.minimizeTimespanChartPoints(chartPoints, CHART_MAX_COLUMNS)
        if (dataset.isEmpty()) return report

        val configurationReport = ConfigurationReport(
            values = chartPoints.map {
                ChartPoint(it.value, it.getTimespanString())
            },
            maxValue = dataset.maxOf { it.value },
            minValue = dataset.minOf { it.value }
        )

        return report.apply {
            this.configurationReport = configurationReport
        }
    }

}
