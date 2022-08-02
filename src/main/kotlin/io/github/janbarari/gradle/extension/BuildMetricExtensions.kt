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
package io.github.janbarari.gradle.extension

import io.github.janbarari.gradle.analytics.domain.model.TimespanChartPoint
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric

fun List<BuildMetric>.mapToInitializationMedianTimespanChartPoints(): List<TimespanChartPoint> {
    return map {
        TimespanChartPoint(
            value = ensureNotNull(it.initializationProcessMetric).median,
            from = it.createdAt,
            to = null
        )
    }
}

fun List<BuildMetric>.mapToInitializationMeanTimespanChartPoints(): List<TimespanChartPoint> {
    return map {
        TimespanChartPoint(
            value = ensureNotNull(it.initializationProcessMetric).mean,
            from = it.createdAt,
            to = null
        )
    }
}

fun List<BuildMetric>.mapToConfigurationMedianTimespanChartPoints(): List<TimespanChartPoint> {
    return map {
        TimespanChartPoint(
            value = ensureNotNull(it.configurationProcessMetric).median,
            from = it.createdAt,
            to = null
        )
    }
}

fun List<BuildMetric>.mapToConfigurationMeanTimespanChartPoints(): List<TimespanChartPoint> {
    return map {
        TimespanChartPoint(
            value = ensureNotNull(it.configurationProcessMetric).mean,
            from = it.createdAt,
            to = null
        )
    }
}

fun List<BuildMetric>.mapToExecutionTimespanChartPoints(): List<TimespanChartPoint> {
    return map {
        TimespanChartPoint(
            value = ensureNotNull(it.executionProcessMetric).median / 1000L,
            from = it.createdAt,
            to = null
        )
    }
}

fun List<BuildMetric>.mapToTotalBuildTimespanChartPoints(): List<TimespanChartPoint> {
    return map {
        TimespanChartPoint(
            value = ensureNotNull(it.overallBuildProcessMetric).median / 1000L,
            from = it.createdAt,
            to = null
        )
    }
}

fun List<BuildMetric>.mapToBuildSuccessRatioTimespanChartPoints(): List<TimespanChartPoint> {
    return map {
        TimespanChartPoint(
            value = ensureNotNull(it.successBuildRateMetric).rate.toLong(),
            from = it.createdAt,
            to = null
        )
    }
}

fun List<BuildMetric>.mapToDependencyResolveTimespanChartPoints(): List<TimespanChartPoint> {
    return map {
        TimespanChartPoint(
            value = ensureNotNull(it.dependencyResolveProcessMetric).median,
            from = it.createdAt,
            to = null
        )
    }
}

fun List<BuildMetric>.mapToParallelRatioTimespanChartPoints(): List<TimespanChartPoint> {
    return map {
        TimespanChartPoint(
            value = ensureNotNull(it.parallelExecutionRateMetric).rate,
            from = it.createdAt,
            to = null
        )
    }
}
