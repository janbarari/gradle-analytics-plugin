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
package io.github.janbarari.gradle.analytics.metric.modulesbuildheatmap.report

import io.github.janbarari.gradle.TowerMockImpl
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.CacheHitMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleBuildHeatmap
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleCacheHit
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesBuildHeatmapMetric
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class CreateModulesBuildHeatmapReportStageTest {

    @Test
    fun `check process() generates report when metric is not available`() = runBlocking {
        val metrics = mutableListOf<BuildMetric>()
        val stage = CreateModulesBuildHeatmapReportStage(TowerMockImpl(), metrics)
        var report = Report("main", "assemble")
        report = stage.process(report)

        assertEquals(0, report.modulesBuildHeatmapReport!!.modules.size)
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
                modulesBuildHeatmap = ModulesBuildHeatmapMetric(
                    listOf(
                        ModuleBuildHeatmap(":woman", 2),
                        ModuleBuildHeatmap(":life", 4),
                        ModuleBuildHeatmap(":freedom", 6)
                    )
                ),
                cacheHitMetric = CacheHitMetric(
                    rate = 70,
                    modules = listOf(
                        ModuleCacheHit(":woman", 60),
                        ModuleCacheHit(":life", 40),
                        ModuleCacheHit(":freedom", 20),
                    )
                ),
                modules = setOf(":woman", ":life", ":freedom")
            )
        )

        metrics.add(
            BuildMetric(
                branch = "main",
                requestedTasks = listOf("assemble"),
                createdAt = 1668936974389,
                gitHeadCommitHash = UUID.randomUUID().toString(),
                modulesBuildHeatmap = ModulesBuildHeatmapMetric(
                    listOf(
                        ModuleBuildHeatmap(":woman", 2),
                        ModuleBuildHeatmap(":life", 4),
                        ModuleBuildHeatmap(":freedom", 6)
                    )
                ),
                cacheHitMetric = CacheHitMetric(
                    rate = 70,
                    modules = listOf(
                        ModuleCacheHit(":woman", 60),
                        ModuleCacheHit(":life", 40),
                        ModuleCacheHit(":freedom", 20),
                    )
                ),
                modules = setOf(":woman", ":life", ":freedom")
            )
        )

        val stage = CreateModulesBuildHeatmapReportStage(TowerMockImpl(), metrics)
        var report = Report("main", "assemble")
        report = stage.process(report)

        assertEquals(":woman", report.modulesBuildHeatmapReport!!.modules[0].path)
        assertEquals(2, report.modulesBuildHeatmapReport!!.modules[0].dependantModulesCount)
        assertEquals(60, report.modulesBuildHeatmapReport!!.modules[0].avgMedianCacheHit)
        assertEquals(2, report.modulesBuildHeatmapReport!!.modules[0].totalBuildCount)

        assertEquals(":life", report.modulesBuildHeatmapReport!!.modules[1].path)
        assertEquals(4, report.modulesBuildHeatmapReport!!.modules[1].dependantModulesCount)
        assertEquals(40, report.modulesBuildHeatmapReport!!.modules[1].avgMedianCacheHit)
        assertEquals(2, report.modulesBuildHeatmapReport!!.modules[1].totalBuildCount)

        assertEquals(":freedom", report.modulesBuildHeatmapReport!!.modules[2].path)
        assertEquals(6, report.modulesBuildHeatmapReport!!.modules[2].dependantModulesCount)
        assertEquals(20, report.modulesBuildHeatmapReport!!.modules[2].avgMedianCacheHit)
        assertEquals(2, report.modulesBuildHeatmapReport!!.modules[2].totalBuildCount)
    }

}
