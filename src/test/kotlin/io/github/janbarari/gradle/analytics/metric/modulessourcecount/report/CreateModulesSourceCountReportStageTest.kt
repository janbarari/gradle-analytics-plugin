/**
 * MIT License
 * Copyright (c) 2024 Mehdi Janbarari (@janbarari)
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
package io.github.janbarari.gradle.analytics.metric.modulessourcecount.report

import io.github.janbarari.gradle.TowerMockImpl
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleSourceCount
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesSourceCountMetric
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.report.CreateModulesSourceCountReportStage
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class CreateModulesSourceCountReportStageTest {

    @Test
    fun `when process() executes with empty metric, expect null report`() = runBlocking {
        val metrics = mutableListOf<BuildMetric>()
        val stage = CreateModulesSourceCountReportStage(TowerMockImpl(), metrics)

        var report = Report("main", "assemble")
        report = stage.process(report)

        assertEquals(null, report.modulesSourceCountReport)
    }

    @Test
    fun `when process() executes with single metric, expect report`() = runBlocking {
        val metrics = mutableListOf<BuildMetric>()
        metrics.add(
            BuildMetric(
                branch = "main",
                requestedTasks = listOf("assemble"),
                createdAt = 1668836798265,
                gitHeadCommitHash = UUID.randomUUID().toString(),
                modules = setOf(":woman", ":life", ":freedom")
            ).apply {
                modulesSourceCountMetric = ModulesSourceCountMetric(
                    listOf(
                        ModuleSourceCount(
                            path = ":woman",
                            value = 120
                        )
                    )
                )
            }
        )

        val stage = CreateModulesSourceCountReportStage(TowerMockImpl(), metrics)

        var report = Report("main", "assemble")
        report = stage.process(report)

        assertEquals(":woman", report.modulesSourceCountReport!!.values[0].path)
        assertEquals(120, report.modulesSourceCountReport!!.values[0].value)
        assertEquals(100.0F, report.modulesSourceCountReport!!.values[0].coverageRate)
        assertEquals(null, report.modulesSourceCountReport!!.values[0].diffRate)
        assertEquals(120, report.modulesSourceCountReport!!.totalSourceCount)
        assertEquals(null, report.modulesSourceCountReport!!.totalDiffRate)
    }

    @Test
    fun `when process() executes with multiple metrics, expect report`() = runBlocking {
        val metrics = mutableListOf<BuildMetric>()
        metrics.add(
            BuildMetric(
                branch = "main",
                requestedTasks = listOf("assemble"),
                createdAt = 1668836798265,
                gitHeadCommitHash = UUID.randomUUID().toString(),
                modules = setOf(":woman", ":life", ":freedom")
            ).apply {
                modulesSourceCountMetric = ModulesSourceCountMetric(
                    listOf(
                        ModuleSourceCount(
                            path = ":woman",
                            value = 128
                        )
                    )
                )
            }
        )
        metrics.add(
            BuildMetric(
                branch = "main",
                requestedTasks = listOf("assemble"),
                createdAt = 1668936974389,
                gitHeadCommitHash = UUID.randomUUID().toString(),
                modules = setOf(":woman", ":life", ":freedom")
            ).apply {
                modulesSourceCountMetric = ModulesSourceCountMetric(
                    listOf(
                        ModuleSourceCount(
                            path = ":woman",
                            value = 256
                        )
                    )
                )
            }
        )

        val stage = CreateModulesSourceCountReportStage(TowerMockImpl(), metrics)

        var report = Report("main", "assemble")
        report = stage.process(report)

        assertEquals(":woman", report.modulesSourceCountReport!!.values[0].path)
        assertEquals(256, report.modulesSourceCountReport!!.values[0].value)
        assertEquals(100.0F, report.modulesSourceCountReport!!.values[0].coverageRate)
        assertEquals(100.0F, report.modulesSourceCountReport!!.values[0].diffRate)
        assertEquals(256, report.modulesSourceCountReport!!.totalSourceCount)
        assertEquals(100.0F, report.modulesSourceCountReport!!.totalDiffRate)
    }

}
