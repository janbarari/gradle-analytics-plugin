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
package io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.report

import io.github.janbarari.gradle.analytics.domain.model.Module
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleExecutionProcess
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesExecutionProcessMetric
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class CreateModulesExecutionProcessReportStageTest {

    @Test
    fun `when process() executes without metric, expect empty report`() = runBlocking {
        val modules = listOf<Module>()
        val metrics = listOf<BuildMetric>()
        val stage = CreateModulesExecutionProcessReportStage(modules, metrics)

        var report = Report("main", "assemble")
        report = stage.process(report)

        assertEquals(0, report.modulesExecutionProcessReport!!.modules.size)
    }

    @Test
    fun `when process() executes with multiple modules, expect report`() = runBlocking {
        val modules = listOf<Module>(
            Module(
                path = ":woman",
                absoluteDir = "woman/fake/directory"
            ),
            Module(
                path = ":life",
                absoluteDir = "life/fake/directory"
            ),
            Module(
                path = ":freedom",
                absoluteDir = "freedom/fake/directory"
            )
        )

        val metrics = mutableListOf<BuildMetric>()
        metrics.add(
            BuildMetric(
                branch = "main",
                requestedTasks = listOf("assemble"),
                createdAt = 1668836798265,
                gitHeadCommitHash = UUID.randomUUID().toString(),
                modules = listOf(":woman", ":life", ":freedom")
            ).apply {
                modulesExecutionProcessMetric = ModulesExecutionProcessMetric(
                    listOf(
                        ModuleExecutionProcess(":woman", 10L, 15L, 50F, 15F),
                        ModuleExecutionProcess(":life", 20L, 35L, 46F, 19F),
                        ModuleExecutionProcess(":freedom", 15L, 39L, 58F, 30F)
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
                modules = listOf(":woman", ":life", ":freedom")
            ).apply {
                modulesExecutionProcessMetric = ModulesExecutionProcessMetric(
                    listOf(
                        ModuleExecutionProcess(":woman", 15L, 20L, 59F, 20F),
                        ModuleExecutionProcess(":life", 25L, 40L, 51F, 24F),
                        ModuleExecutionProcess(":freedom", 20L, 54L, 70F, 34F)
                    )
                )
            }
        )


        val stage = CreateModulesExecutionProcessReportStage(modules, metrics)

        var report = Report("main", "assemble")
        report = stage.process(report)

        //Assert diff rates
        assertEquals(50.0F, report.modulesExecutionProcessReport!!.modules.find { it.path == ":woman" }!!.diffRate)
        assertEquals(25.0F, report.modulesExecutionProcessReport!!.modules.find { it.path == ":life" }!!.diffRate)
        assertEquals(33.33F, report.modulesExecutionProcessReport!!.modules.find { it.path == ":freedom" }!!.diffRate)

        //Assert avg median duration
        assertEquals(12L, report.modulesExecutionProcessReport!!.modules.find { it.path == ":woman" }!!.avgMedianExecInMillis)
        assertEquals(22L, report.modulesExecutionProcessReport!!.modules.find { it.path == ":life" }!!.avgMedianExecInMillis)
        assertEquals(17L, report.modulesExecutionProcessReport!!.modules.find { it.path == ":freedom" }!!.avgMedianExecInMillis)

        //Assert avg median parallel rate
        assertEquals(54.5F, report.modulesExecutionProcessReport!!.modules.find { it.path == ":woman" }!!.avgMedianParallelRate)
        assertEquals(48.5F, report.modulesExecutionProcessReport!!.modules.find { it.path == ":life" }!!.avgMedianParallelRate)
        assertEquals(
            64.0F,
            report.modulesExecutionProcessReport!!.modules.find { it.path == ":freedom" }!!.avgMedianParallelRate
        )

        //Assert avg median coverage
        assertEquals(17.5F, report.modulesExecutionProcessReport!!.modules.find { it.path == ":woman" }!!.avgMedianCoverageRate)
        assertEquals(21.5F, report.modulesExecutionProcessReport!!.modules.find { it.path == ":life" }!!.avgMedianCoverageRate)
        assertEquals(
            32.0F,
            report.modulesExecutionProcessReport!!.modules.find { it.path == ":freedom" }!!.avgMedianCoverageRate
        )

    }

}
