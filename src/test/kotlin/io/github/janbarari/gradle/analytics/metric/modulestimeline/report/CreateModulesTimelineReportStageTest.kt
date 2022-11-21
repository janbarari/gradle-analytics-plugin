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
package io.github.janbarari.gradle.analytics.metric.modulestimeline.report

import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleTimeline
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesTimelineMetric
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.analytics.domain.usecase.GetModulesTimelineUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CreateModulesTimelineReportStageTest {

    @Test
    fun `check process() generates report when report is not available`() = runBlocking {
        val getModulesTimelineUseCase: GetModulesTimelineUseCase = mockk()
        coEvery {
            getModulesTimelineUseCase.execute(any())
        }.returns(null)

        val stage = CreateModulesTimelineReportStage("main", getModulesTimelineUseCase)
        var report = Report("main", "assemble")
        report = stage.process(report)

        assertEquals(null, report.modulesSourceSizeReport)
    }

    @Test
    fun `check process() generates report when report is available`() = runBlocking {
        val getModulesTimelineUseCase: GetModulesTimelineUseCase = mockk()
        coEvery {
            getModulesTimelineUseCase.execute(any())
        }.returns(
            ModulesTimelineMetric(
                start = 0,
                end = 100,
                modules = listOf(
                    ModuleTimeline(
                        path = ":woman",
                        timelines = listOf(
                            ModuleTimeline.Timeline(
                                path = ":task1",
                                start = 0,
                                end = 50,
                                isCached = true
                            ),
                            ModuleTimeline.Timeline(
                                path = ":task2",
                                start = 50,
                                end = 100,
                                isCached = false
                            )
                        )
                    ),
                    ModuleTimeline(
                        path = ":life",
                        timelines = listOf(
                            ModuleTimeline.Timeline(
                                path = ":task1",
                                start = 0,
                                end = 50,
                                isCached = true
                            ),
                            ModuleTimeline.Timeline(
                                path = ":task2",
                                start = 50,
                                end = 100,
                                isCached = false
                            )
                        )
                    ),
                    ModuleTimeline(
                        path = ":freedom",
                        timelines = listOf(
                            ModuleTimeline.Timeline(
                                path = ":task1",
                                start = 0,
                                end = 50,
                                isCached = true
                            ),
                            ModuleTimeline.Timeline(
                                path = ":task2",
                                start = 50,
                                end = 100,
                                isCached = false
                            )
                        )
                    )
                ),
                createdAt = 1668836798265
            )
        )

        val stage = CreateModulesTimelineReportStage("main", getModulesTimelineUseCase)
        var report = Report("main", "assemble")
        report = stage.process(report)

        assertEquals(0, report.modulesTimelineReport!!.start)
        assertEquals(100, report.modulesTimelineReport!!.end)
        assertEquals(3, report.modulesTimelineReport!!.modules.size)
        assertEquals(1668836798265, report.modulesTimelineReport!!.createdAt)
    }

}
