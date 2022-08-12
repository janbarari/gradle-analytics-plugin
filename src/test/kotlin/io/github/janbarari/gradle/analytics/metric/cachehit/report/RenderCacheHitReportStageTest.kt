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

import io.github.janbarari.gradle.analytics.domain.model.ChartPoint
import io.github.janbarari.gradle.analytics.domain.model.report.CacheHitReport
import io.github.janbarari.gradle.analytics.domain.model.report.ModuleCacheHitReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class RenderCacheHitReportStageTest {

    @Test
    fun `When the stage proceeds with positive data, expects the metric report`() = runBlocking {
        val cacheHitReport = CacheHitReport(
            modules = listOf(
                ModuleCacheHitReport(
                    path = ":core",
                    rate = 43L,
                    diffRate = 19F,
                    meanValues = listOf(
                        ChartPoint(19L, "12/04/2022"),
                        ChartPoint(25L, "13/04/2022"),
                        ChartPoint(30L, "14/04/2022")
                    )
                )
            ),
            overallMeanValues = listOf(
                ChartPoint(12L, "12/04/2022"),
                ChartPoint(31L, "13/04/2022"),
                ChartPoint(39L, "14/04/2022")
            ),
            overallRate = 63L,
            overallDiffRate = 22.0F
        )
        val report = Report(
            branch = "master",
            requestedTasks = "assemble",
            cacheHitReport = cacheHitReport
        )
        val stage = RenderCacheHitReportStage(report)

        var renderedResult = "%cache-hit-metric%"
        renderedResult = stage.process(renderedResult)

        println(renderedResult)

        assertTrue {
            renderedResult.contains("var _values = [12, 31, 39];") &&
            renderedResult.contains("var _labels = [\"12/04/2022\",\"13/04/2022\",\"14/04/2022\"];") &&
            renderedResult.lines().first() == "<h2>Cache Hit</h2>" &&
            renderedResult.contains("<td>:core</td>") &&
            renderedResult.contains("<td>43%</td>")
        }
    }

    @Test
    fun `When the stage proceeds with negative data, expects the metric report`() = runBlocking {
        val cacheHitReport = CacheHitReport(
            modules = listOf(
                ModuleCacheHitReport(
                    path = ":core",
                    rate = 43L,
                    diffRate = -19F,
                    meanValues = listOf(
                        ChartPoint(19L, "12/04/2022"),
                        ChartPoint(25L, "13/04/2022"),
                        ChartPoint(30L, "14/04/2022")
                    )
                ),
                ModuleCacheHitReport(
                    path = ":architecture",
                    rate = 43L,
                    diffRate = 0F,
                    meanValues = listOf(
                        ChartPoint(19L, "12/04/2022"),
                        ChartPoint(25L, "13/04/2022"),
                        ChartPoint(30L, "14/04/2022")
                    )
                )
            ),
            overallMeanValues = listOf(
                ChartPoint(12L, "12/04/2022"),
                ChartPoint(31L, "13/04/2022"),
                ChartPoint(39L, "14/04/2022")
            ),
            overallRate = 63L,
            overallDiffRate = -22.0F
        )
        val report = Report(
            branch = "master",
            requestedTasks = "assemble",
            cacheHitReport = cacheHitReport
        )
        val stage = RenderCacheHitReportStage(report)

        var renderedResult = "%cache-hit-metric%"
        renderedResult = stage.process(renderedResult)

        println(renderedResult)

        assertTrue {
            renderedResult.contains("var _values = [12, 31, 39];") &&
                    renderedResult.contains("var _labels = [\"12/04/2022\",\"13/04/2022\",\"14/04/2022\"];") &&
                    renderedResult.lines().first() == "<h2>Cache Hit</h2>" &&
                    renderedResult.contains("<td>:core</td>") &&
                    renderedResult.contains("<td>43%</td>")
        }
    }

    @Test
    fun `When the stage proceeds with empty data, expects the metric report`() = runBlocking {
        val cacheHitReport = CacheHitReport(
            modules = emptyList(),
            overallMeanValues = listOf(
                ChartPoint(12L, "12/04/2022"),
                ChartPoint(31L, "13/04/2022"),
                ChartPoint(39L, "14/04/2022")
            ),
            overallRate = 22L,
            overallDiffRate = -43.0F
        )
        val report = Report(
            branch = "master",
            requestedTasks = "assemble",
            cacheHitReport = cacheHitReport
        )
        val stage = RenderCacheHitReportStage(report)

        var renderedResult = "%cache-hit-metric%"
        renderedResult = stage.process(renderedResult)

        assertTrue {
            renderedResult.contains("var _values = [12, 31, 39];") &&
                    renderedResult.contains("var _labels = [\"12/04/2022\",\"13/04/2022\",\"14/04/2022\"];") &&
                    renderedResult.lines().first() == "<h2>Cache Hit</h2>"
        }
    }

    @Test
    fun `When the stage proceeds with no diff rate, expects the metric report`() = runBlocking {
        val cacheHitReport = CacheHitReport(
            modules = emptyList(),
            overallMeanValues = listOf(
                ChartPoint(12L, "12/04/2022"),
                ChartPoint(31L, "13/04/2022"),
                ChartPoint(39L, "14/04/2022")
            ),
            overallRate = -22L,
            overallDiffRate = 0F
        )
        val report = Report(
            branch = "master",
            requestedTasks = "assemble",
            cacheHitReport = cacheHitReport
        )
        val stage = RenderCacheHitReportStage(report)

        var renderedResult = "%cache-hit-metric%"
        renderedResult = stage.process(renderedResult)

        assertTrue {
            renderedResult.contains("var _values = [12, 31, 39];") &&
                    renderedResult.contains("var _labels = [\"12/04/2022\",\"13/04/2022\",\"14/04/2022\"];") &&
                    renderedResult.lines().first() == "<h2>Cache Hit</h2>"
        }
    }

    @Test
    fun `When the stage proceeds with null data, expects the empty message`() = runBlocking {
        val report = Report(
            branch = "master",
            requestedTasks = "assemble"
        )
        val stage = RenderCacheHitReportStage(report)

        var renderedResult = "%cache-hit-metric%"
        renderedResult = stage.process(renderedResult)

        assertTrue {
            renderedResult.contains("Cache Hit is not available!")
        }
    }


}
