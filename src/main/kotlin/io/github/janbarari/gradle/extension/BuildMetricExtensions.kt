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

import io.github.janbarari.gradle.analytics.domain.model.TimespanPoint
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric

/**
 * Map the InitializationProcessMetric collection to TimespanPoint median collection.
 */
fun List<BuildMetric>.mapToInitializationMedianTimespanChartPoints(): List<TimespanPoint> {
    return map {
        TimespanPoint(
            value = it.initializationProcessMetric!!.median,
            from = it.createdAt,
            to = null
        )
    }
}

/**
 * Map the InitializationProcessMetric collection to TimespanPoint mean collection.
 */
fun List<BuildMetric>.mapToInitializationMeanTimespanChartPoints(): List<TimespanPoint> {
    return map {
        TimespanPoint(
            value = it.initializationProcessMetric!!.mean,
            from = it.createdAt,
            to = null
        )
    }
}

/**
 * Map the ConfigurationProcessMetric collection to TimespanPoint median collection.
 */
fun List<BuildMetric>.mapToConfigurationMedianTimespanChartPoints(): List<TimespanPoint> {
    return map {
        TimespanPoint(
            value = it.configurationProcessMetric!!.median,
            from = it.createdAt,
            to = null
        )
    }
}

/**
 * Map the ConfigurationProcessMetric collection to TimespanPoint mean collection.
 */
fun List<BuildMetric>.mapToConfigurationMeanTimespanChartPoints(): List<TimespanPoint> {
    return map {
        TimespanPoint(
            value = it.configurationProcessMetric!!.mean,
            from = it.createdAt,
            to = null
        )
    }
}

/**
 * Map the ExecutionProcessMetric collection to TimespanPoint median collection.
 */
fun List<BuildMetric>.mapToExecutionMedianTimespanChartPoints(): List<TimespanPoint> {
    return map {
        TimespanPoint(
            value = it.executionProcessMetric!!.median / 1000L,
            from = it.createdAt,
            to = null
        )
    }
}
/**
 * Map the ExecutionProcessMetric collection to TimespanPoint mean collection.
 */
fun List<BuildMetric>.mapToExecutionMeanTimespanChartPoints(): List<TimespanPoint> {
    return map {
        TimespanPoint(
            value = it.executionProcessMetric!!.mean / 1000L,
            from = it.createdAt,
            to = null
        )
    }
}

/**
 * Map OverallBuildProcessMetric collection to TimespanPoint median collection.
 */
fun List<BuildMetric>.mapToOverallBuildProcessMedianTimespanChartPoints(): List<TimespanPoint> {
    return map {
        TimespanPoint(
            value = it.overallBuildProcessMetric!!.median / 1000L,
            from = it.createdAt,
            to = null
        )
    }
}

/**
 * Map the OverallBuildProcessMetric collection to TimespanPoint mean collection.
 */
fun List<BuildMetric>.mapToOverallBuildProcessMeanTimespanChartPoints(): List<TimespanPoint> {
    return map {
        TimespanPoint(
            value = it.overallBuildProcessMetric!!.mean / 1000L,
            from = it.createdAt,
            to = null
        )
    }
}

/**
 * Map the SuccessBuildRateProcess Metric collection to TimespanPoint median collection.
 */
fun List<BuildMetric>.mapToSuccessBuildRateMedianTimespanChartPoints(): List<TimespanPoint> {
    return map {
        TimespanPoint(
            value = it.successBuildRateMetric!!.medianRate.toLong(),
            from = it.createdAt,
            to = null
        )
    }
}

/**
 * Map the SuccessBuildRateProcess Metric collection to TimespanPoint mean collection.
 */
fun List<BuildMetric>.mapToSuccessBuildRateMeanTimespanChartPoints(): List<TimespanPoint> {
    return map {
        TimespanPoint(
            value = it.successBuildRateMetric!!.meanRate.toLong(),
            from = it.createdAt,
            to = null
        )
    }
}

/**
 * Map the DependencyResolveProcessMetric Metric collection to TimespanPoint median collection.
 */
fun List<BuildMetric>.mapToDependencyResolveMedianTimespanChartPoints(): List<TimespanPoint> {
    return map {
        TimespanPoint(
            value = it.dependencyResolveProcessMetric!!.median,
            from = it.createdAt,
            to = null
        )
    }
}

/**
 * Map the DependencyResolveProcessMetric Metric collection to TimespanPoint mean collection.
 */
fun List<BuildMetric>.mapToDependencyResolveMeanTimespanChartPoints(): List<TimespanPoint> {
    return map {
        TimespanPoint(
            value = it.dependencyResolveProcessMetric!!.mean,
            from = it.createdAt,
            to = null
        )
    }
}

/**
 * Map the ParallelExecutionRateMetric Metric collection to TimespanPoint median collection.
 */
fun List<BuildMetric>.mapToParallelExecutionRateMedianTimespanPoints(): List<TimespanPoint> {
    return map {
        TimespanPoint(
            value = it.parallelExecutionRateMetric!!.medianRate,
            from = it.createdAt,
            to = null
        )
    }
}
