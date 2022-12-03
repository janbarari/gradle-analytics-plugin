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
package io.github.janbarari.gradle.analytics.metric.buildstatus.report

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.CacheHitMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ConfigurationProcessMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.DependencyResolveProcessMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ExecutionProcessMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.InitializationProcessMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleCacheHit
import io.github.janbarari.gradle.analytics.domain.model.metric.OverallBuildProcessMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ParallelExecutionRateMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.SuccessBuildRateMetric
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class CreateBuildStatusReportStageTest {

    @Test
    fun `check process() generates report when metric is not available`() = runBlocking {
        val metrics = mutableListOf<BuildMetric>()

        val stage = CreateBuildStatusReportStage(metrics)
        var report = Report("main", "assemble")
        report = stage.process(report)

        assertEquals(0, report.buildStatusReport!!.cumulativeOverallBuildProcessBySeconds)
        assertEquals(0, report.buildStatusReport!!.avgOverallBuildProcessBySeconds)
        assertEquals(0, report.buildStatusReport!!.totalBuildProcessCount)
        assertEquals(0, report.buildStatusReport!!.totalProjectModulesCount)
        assertEquals(0, report.buildStatusReport!!.cumulativeParallelExecutionBySeconds)
        assertEquals(0F, report.buildStatusReport!!.avgParallelExecutionRate)
        assertEquals(0, report.buildStatusReport!!.totalSucceedBuildCount)
        assertEquals(0, report.buildStatusReport!!.totalFailedBuildCount)
        assertEquals(0F, report.buildStatusReport!!.avgCacheHitRate)
        assertEquals(0, report.buildStatusReport!!.cumulativeDependencyResolveBySeconds)
        assertEquals(0, report.buildStatusReport!!.avgInitializationProcessByMillis)
        assertEquals(0, report.buildStatusReport!!.avgConfigurationProcessByMillis)
        assertEquals(0, report.buildStatusReport!!.avgExecutionProcessBySeconds)
    }

    @Test
    fun `check process() generates report when metric is available`() = runBlocking {
        val metrics = mutableListOf<BuildMetric>()

        metrics.add(
            BuildMetric(
                branch = "main",
                requestedTasks = listOf("assemble"),
                createdAt = 1668836798265,
                gitHeadCommitHash = UUID.randomUUID().toString(),
                modules = listOf(
                    ":woman",
                    ":life",
                    ":freedom"
                ),
                overallBuildProcessMetric = OverallBuildProcessMetric(median = 100L, mean = 135L),
                dependencyResolveProcessMetric = DependencyResolveProcessMetric(median = 50L, mean = 50L),
                executionProcessMetric = ExecutionProcessMetric(median = 1500L, mean = 1750L),
                parallelExecutionRateMetric = ParallelExecutionRateMetric(35),
                successBuildRateMetric = SuccessBuildRateMetric(100F, meanRate = 100F, successes = 12, fails = 1),
                cacheHitMetric = CacheHitMetric(rate = 45, modules = listOf(
                    ModuleCacheHit(path = ":woman", 12),
                    ModuleCacheHit(path = ":life", 29),
                    ModuleCacheHit(path = ":freedom", 46)
                )),
                initializationProcessMetric = InitializationProcessMetric(median = 100, mean = 120),
                configurationProcessMetric = ConfigurationProcessMetric(median = 110, mean = 140)
            )
        )

        metrics.add(
            BuildMetric(
                branch = "main",
                requestedTasks = listOf("assemble"),
                createdAt = 1668936974389,
                gitHeadCommitHash = UUID.randomUUID().toString(),
                modules = listOf(
                    ":woman",
                    ":life",
                    ":freedom"
                ),
                overallBuildProcessMetric = OverallBuildProcessMetric(median = 80L, mean = 98L),
                dependencyResolveProcessMetric = DependencyResolveProcessMetric(median = 69L, mean = 64L),
                executionProcessMetric = ExecutionProcessMetric(median = 1000L, mean = 1450L),
                parallelExecutionRateMetric = ParallelExecutionRateMetric(58),
                successBuildRateMetric = SuccessBuildRateMetric(100F, meanRate = 100F, successes = 19, fails = 3),
                cacheHitMetric = CacheHitMetric(rate = 78, modules = listOf(
                    ModuleCacheHit(path = ":woman", 34),
                    ModuleCacheHit(path = ":life", 45),
                    ModuleCacheHit(path = ":freedom", 68)
                )),
                initializationProcessMetric = InitializationProcessMetric(median = 140, mean = 160),
                configurationProcessMetric = ConfigurationProcessMetric(median = 190, mean = 230)
            )
        )

        val stage = CreateBuildStatusReportStage(metrics)
        var report = Report("main", "assemble")
        report = stage.process(report)

        assertEquals(0, report.buildStatusReport!!.cumulativeOverallBuildProcessBySeconds)
        assertEquals(0, report.buildStatusReport!!.avgOverallBuildProcessBySeconds)
        assertEquals(2, report.buildStatusReport!!.totalBuildProcessCount)
        assertEquals(3, report.buildStatusReport!!.totalProjectModulesCount)
        assertEquals(2, report.buildStatusReport!!.cumulativeParallelExecutionBySeconds)
        assertEquals(46.5F, report.buildStatusReport!!.avgParallelExecutionRate)
        assertEquals(31, report.buildStatusReport!!.totalSucceedBuildCount)
        assertEquals(4, report.buildStatusReport!!.totalFailedBuildCount)
        assertEquals(61.5F, report.buildStatusReport!!.avgCacheHitRate)
        assertEquals(0, report.buildStatusReport!!.cumulativeDependencyResolveBySeconds)
        assertEquals(120, report.buildStatusReport!!.avgInitializationProcessByMillis)
        assertEquals(150, report.buildStatusReport!!.avgConfigurationProcessByMillis)
        assertEquals(1, report.buildStatusReport!!.avgExecutionProcessBySeconds)
    }

}
