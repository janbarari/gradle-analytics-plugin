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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.extension

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ConfigurationProcessMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.DependencyResolveProcessMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ExecutionProcessMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.InitializationProcessMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.OverallBuildProcessMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ParallelExecutionRateMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.SuccessBuildRateMetric
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BuildMetricExtensionsTest {

    @Test
    fun `Check InitializationMedianTimespanChartPoint mapper return correct result`() {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "master",
                requestedTasks = listOf("assemble"),
                createdAt = 1660318217387,
                gitHeadCommitHash = "unknown",
                initializationProcessMetric = InitializationProcessMetric(
                    median = 1000L,
                    mean = 1000L
                )
            )
        )
        val result = buildMetrics.mapToInitializationMedianTimespanChartPoints()
        assertEquals(1000L, result.first().value)
        assertEquals(1660318217387, result.first().from)
        assertEquals(null, result.first().to)
    }

    @Test
    fun `Check InitializationMeanTimespanChartPoint mapper return correct result`() {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "master",
                requestedTasks = listOf("assemble"),
                createdAt = 1660318217387,
                gitHeadCommitHash = "unknown",
                initializationProcessMetric = InitializationProcessMetric(
                    median = 1000L,
                    mean = 1000L
                )
            )
        )
        val result = buildMetrics.mapToInitializationMeanTimespanChartPoints()
        assertEquals(1000L, result.first().value)
        assertEquals(1660318217387, result.first().from)
        assertEquals(null, result.first().to)
    }

    @Test
    fun `Check ConfigurationMedianTimespanChartPoint mapper return correct result`() {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "master",
                requestedTasks = listOf("assemble"),
                createdAt = 1660318217387,
                gitHeadCommitHash = "unknown",
                configurationProcessMetric = ConfigurationProcessMetric(
                    median = 1000L,
                    mean = 1000L
                )
            )
        )
        val result = buildMetrics.mapToConfigurationMedianTimespanChartPoints()
        assertEquals(1000L, result.first().value)
        assertEquals(1660318217387, result.first().from)
        assertEquals(null, result.first().to)
    }

    @Test
    fun `Check ConfigurationMeanTimespanChartPoint mapper return correct result`() {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "master",
                requestedTasks = listOf("assemble"),
                createdAt = 1660318217387,
                gitHeadCommitHash = "unknown",
                configurationProcessMetric = ConfigurationProcessMetric(
                    median = 1000L,
                    mean = 1000L
                )
            )
        )
        val result = buildMetrics.mapToConfigurationMeanTimespanChartPoints()
        assertEquals(1000L, result.first().value)
        assertEquals(1660318217387, result.first().from)
        assertEquals(null, result.first().to)
    }

    @Test
    fun `Check ExecutionMedianTimespanChartPoint mapper return correct result`() {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "master",
                requestedTasks = listOf("assemble"),
                createdAt = 1660318217387,
                gitHeadCommitHash = "unknown",
                executionProcessMetric = ExecutionProcessMetric(
                    median = 1000L,
                    mean = 1000L
                )
            )
        )
        val result = buildMetrics.mapToExecutionMedianTimespanChartPoints()
        assertEquals(1L, result.first().value)
        assertEquals(1660318217387, result.first().from)
        assertEquals(null, result.first().to)
    }

    @Test
    fun `Check ExecutionMeanTimespanChartPoint mapper return correct result`() {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "master",
                requestedTasks = listOf("assemble"),
                createdAt = 1660318217387,
                gitHeadCommitHash = "unknown",
                executionProcessMetric = ExecutionProcessMetric(
                    median = 1000L,
                    mean = 1000L
                )
            )
        )
        val result = buildMetrics.mapToExecutionMeanTimespanChartPoints()
        assertEquals(1L, result.first().value)
        assertEquals(1660318217387, result.first().from)
        assertEquals(null, result.first().to)
    }

    @Test
    fun `Check OverallBuildProcessMedianTimespanChartPoint mapper return correct result`() {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "master",
                requestedTasks = listOf("assemble"),
                createdAt = 1660318217387,
                gitHeadCommitHash = "unknown",
                overallBuildProcessMetric = OverallBuildProcessMetric(
                    median = 1000L,
                    mean = 1000L
                )
            )
        )
        val result = buildMetrics.mapToOverallBuildProcessMedianTimespanChartPoints()
        assertEquals(1L, result.first().value)
        assertEquals(1660318217387, result.first().from)
        assertEquals(null, result.first().to)
    }

    @Test
    fun `Check OverallBuildProcessMeanTimespanChartPoint mapper return correct result`() {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "master",
                requestedTasks = listOf("assemble"),
                createdAt = 1660318217387,
                gitHeadCommitHash = "unknown",
                overallBuildProcessMetric = OverallBuildProcessMetric(
                    median = 1000L,
                    mean = 1000L
                )
            )
        )
        val result = buildMetrics.mapToOverallBuildProcessMeanTimespanChartPoints()
        assertEquals(1L, result.first().value)
        assertEquals(1660318217387, result.first().from)
        assertEquals(null, result.first().to)
    }

    @Test
    fun `Check SuccessBuildRateMedianTimespanChartPoint mapper return correct result`() {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "master",
                requestedTasks = listOf("assemble"),
                createdAt = 1660318217387,
                gitHeadCommitHash = "unknown",
                successBuildRateMetric = SuccessBuildRateMetric(
                    medianRate = 100F,
                    meanRate = 100F
                )
            )
        )
        val result = buildMetrics.mapToSuccessBuildRateMedianTimespanChartPoints()
        assertEquals(100, result.first().value)
        assertEquals(1660318217387, result.first().from)
        assertEquals(null, result.first().to)
    }

    @Test
    fun `Check SuccessBuildRateMeanTimespanChartPoint mapper return correct result`() {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "master",
                requestedTasks = listOf("assemble"),
                createdAt = 1660318217387,
                gitHeadCommitHash = "unknown",
                successBuildRateMetric = SuccessBuildRateMetric(
                    medianRate = 100F,
                    meanRate = 100F
                )
            )
        )
        val result = buildMetrics.mapToSuccessBuildRateMeanTimespanChartPoints()
        assertEquals(100, result.first().value)
        assertEquals(1660318217387, result.first().from)
        assertEquals(null, result.first().to)
    }

    @Test
    fun `Check DependencyResolveMedianTimespanChartPoint mapper return correct result`() {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "master",
                requestedTasks = listOf("assemble"),
                createdAt = 1660318217387,
                gitHeadCommitHash = "unknown",
                dependencyResolveProcessMetric = DependencyResolveProcessMetric(
                    mean = 1000L,
                    median = 1000L
                )
            )
        )
        val result = buildMetrics.mapToDependencyResolveMedianTimespanChartPoints()
        assertEquals(1000L, result.first().value)
        assertEquals(1660318217387, result.first().from)
        assertEquals(null, result.first().to)
    }

    @Test
    fun `Check DependencyResolveMeanTimespanChartPoint mapper return correct result`() {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "master",
                requestedTasks = listOf("assemble"),
                createdAt = 1660318217387,
                gitHeadCommitHash = "unknown",
                dependencyResolveProcessMetric = DependencyResolveProcessMetric(
                    mean = 1000L,
                    median = 1000L
                )
            )
        )
        val result = buildMetrics.mapToDependencyResolveMeanTimespanChartPoints()
        assertEquals(1000L, result.first().value)
        assertEquals(1660318217387, result.first().from)
        assertEquals(null, result.first().to)
    }

    @Test
    fun `Check ParallelRatioTimespanChartPoint mapper return correct result`() {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "master",
                requestedTasks = listOf("assemble"),
                createdAt = 1660318217387,
                gitHeadCommitHash = "unknown",
                parallelExecutionRateMetric = ParallelExecutionRateMetric(
                    medianRate = 90L
                )
            )
        )
        val result = buildMetrics.mapToParallelExecutionRateMedianTimespanPoints()
        assertEquals(90L, result.first().value)
        assertEquals(1660318217387, result.first().from)
        assertEquals(null, result.first().to)
    }

}

