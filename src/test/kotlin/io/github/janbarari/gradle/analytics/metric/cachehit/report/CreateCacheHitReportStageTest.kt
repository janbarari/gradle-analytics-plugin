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
package io.github.janbarari.gradle.analytics.metric.cachehit.report

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.CacheHitMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleCacheHit
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.extension.hasMultipleItems
import io.github.janbarari.gradle.extension.hasSingleItem
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.isNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateCacheHitReportStageTest {

    lateinit var stage: CreateCacheHitReportStage

    private val sampleBuildMetricWithCacheHit2 = BuildMetric(
        branch = "develop",
        gitHeadCommitHash = "unknown",
        requestedTasks = listOf(
            "assemble"
        ),
        createdAt = 16588904332,
        cacheHitMetric = CacheHitMetric(
            rate = 88L,
            modules = listOf(
                ModuleCacheHit(
                    rate = 70,
                    path = ":core"
                ),
                ModuleCacheHit(
                    rate = 30,
                    path = ":domain"
                ),
                ModuleCacheHit(
                    rate = 34,
                    path = ":data"
                )
            )
        ),
        modules = listOf(":core", ":domain", ":data")
    )

    private val sampleBuildMetricWithCacheHit = BuildMetric(
        branch = "develop",
        gitHeadCommitHash = "unknown",
        requestedTasks = listOf(
            "assemble"
        ),
        createdAt = 16588904332,
        cacheHitMetric = CacheHitMetric(
            rate = 59L,
            modules = listOf(
                ModuleCacheHit(
                    rate = 45,
                    path = ":core"
                ),
                ModuleCacheHit(
                    rate = 75,
                    path = ":domain"
                ),
                ModuleCacheHit(
                    rate = 34,
                    path = ":data"
                )
            )
        ),
        modules = listOf(":core", ":domain", ":data")
    )

    private val sampleBuildMetricWithoutCacheHit = BuildMetric(
        branch = "develop",
        requestedTasks = listOf(
            "assemble"
        ),
        createdAt = 16588904332,
        gitHeadCommitHash = "unknown",
        modules = emptyList()
    )

    @Test
    fun `When the stage proceeds with single data, expect the report to be generated`() = runBlocking {
        val buildMetrics = listOf(
            sampleBuildMetricWithoutCacheHit,
            sampleBuildMetricWithCacheHit
        )

        stage = CreateCacheHitReportStage(buildMetrics)

        var report = Report(
            branch = "master",
            requestedTasks = "assemble"
        )

        report = stage.process(report)

        assertTrue {
            report.cacheHitReport.isNotNull()
        }
        assertTrue {
            report.cacheHitReport?.overallDiffRate.isNull()
        }
        assertEquals(59L, report.cacheHitReport?.overallRate)
        assertTrue {
            report.cacheHitReport?.overallMeanValues?.hasSingleItem() == true
        }
        assertTrue {
            report.cacheHitReport?.modules?.size == 3
        }
    }

    @Test
    fun `When the stage proceeds with multiple data, expect the report to be generated`() = runBlocking {
        val buildMetrics = listOf(
            sampleBuildMetricWithoutCacheHit,
            sampleBuildMetricWithCacheHit,
            sampleBuildMetricWithCacheHit2
        )

        stage = CreateCacheHitReportStage(buildMetrics)

        var report = Report(
            branch = "master",
            requestedTasks = "assemble"
        )

        report = stage.process(report)

        assertTrue {
            report.cacheHitReport.isNotNull()
        }
        assertEquals(88, report.cacheHitReport?.overallRate)
        assertEquals(49.15F, report.cacheHitReport?.overallDiffRate)
        assertTrue {
            report.cacheHitReport?.overallMeanValues?.hasMultipleItems() == true
        }
        assertEquals(3, report.cacheHitReport?.modules?.size)
        assertEquals(0.0F, report.cacheHitReport?.modules?.find { it.path == ":data" }!!.diffRate)
        assertEquals(34, report.cacheHitReport?.modules?.find { it.path == ":data" }!!.rate)
        assertEquals(-60.01F, report.cacheHitReport?.modules?.find { it.path == ":domain" }!!.diffRate)
        assertEquals(30, report.cacheHitReport?.modules?.find { it.path == ":domain" }!!.rate)
        assertEquals(55.55F, report.cacheHitReport?.modules?.find { it.path == ":core" }!!.diffRate)
        assertEquals(70, report.cacheHitReport?.modules?.find { it.path == ":core" }!!.rate)
    }

    @Test
    fun `When the stage proceeds with empty data, expect the report to be skipped`() = runBlocking {
        val buildMetrics = emptyList<BuildMetric>()

        stage = CreateCacheHitReportStage(buildMetrics)

        var report = Report(
            branch = "master",
            requestedTasks = "assemble"
        )

        report = stage.process(report)

        assertTrue {
            report.cacheHitReport.isNull()
        }
    }

}
