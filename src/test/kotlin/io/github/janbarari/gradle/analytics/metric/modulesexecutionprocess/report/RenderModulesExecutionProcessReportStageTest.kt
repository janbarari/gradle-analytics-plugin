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
package io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.report

import io.github.janbarari.gradle.TowerMockImpl
import io.github.janbarari.gradle.analytics.domain.model.TimespanPoint
import io.github.janbarari.gradle.analytics.domain.model.report.ModuleExecutionProcess
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesExecutionProcessReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RenderModulesExecutionProcessReportStageTest {

    @Test
    fun `check render when report is null`() = runBlocking {
        val report = Report("main", "assemble")

        val renderTemplate = "%modules-execution-process-metric%"
        val stage = RenderModulesExecutionProcessReportStage(TowerMockImpl(), report)
        val result = stage.process(renderTemplate)

        val expectedAnswer = "<p>Modules Execution Process is not available!</p><div class=\"space\"></div>"
        assertEquals(expectedAnswer, result)
    }

    @Test
    fun `check render when data is empty`() = runBlocking {
        val data = mutableListOf<ModuleExecutionProcess>()

        val report = Report("main", "assemble")
        report.modulesExecutionProcessReport = ModulesExecutionProcessReport(data)

        val renderTemplate = "%modules-execution-process-metric%"
        val stage = RenderModulesExecutionProcessReportStage(TowerMockImpl(), report)
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("labels: []") &&
            result.contains("<table>\n" +
                    "            <tr>\n" +
                    "                <th>#</th>\n" +
                    "                <th>Module</th>\n" +
                    "                <th>Avg Duration</th>\n" +
                    "                <th>Avg Parallel Duration</th>\n" +
                    "                <th>Avg Parallel Rate</th>\n" +
                    "                <th>Avg Coverage</th>\n" +
                    "                <th>Duration Diff Rate</th>\n" +
                    "            </tr>\n" +
                    "            \n" +
                    "        </table>")
        }
    }

    @Test
    fun `check render when diffRate is positive`() = runBlocking {
        val data = mutableListOf<ModuleExecutionProcess>()

        data.add(
            ModuleExecutionProcess(
                path = ":woman",
                avgMedianExecInMillis = 1000L,
                avgMedianParallelExecInMillis = 2000L,
                avgMedianParallelRate = 97F,
                avgMedianCoverageRate = 16F,
                avgMedianExecs = listOf(
                    TimespanPoint(10L, 1668180871238, 1668280871238),
                    TimespanPoint(20L, 1668280871238, 1668380871238),
                    TimespanPoint(15L, 1668380871238, 1668480871238)
                ),
                diffRate = 10F
            )
        )

        val report = Report("main", "assemble")
        report.modulesExecutionProcessReport = ModulesExecutionProcessReport(data)

        val renderTemplate = "%modules-execution-process-metric%"
        val stage = RenderModulesExecutionProcessReportStage(TowerMockImpl(), report)
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("<tr><th>1</th><th>:woman</th><th>1s</th><th>2s</th><th>97.0%</th><th>16.0%</th><th class=\"red\">+10.0%</th></tr>")
        }
    }

    @Test
    fun `check render when diffRate is negative`() = runBlocking {
        val data = mutableListOf<ModuleExecutionProcess>()

        data.add(
            ModuleExecutionProcess(
                path = ":woman",
                avgMedianExecInMillis = 1000L,
                avgMedianParallelExecInMillis = 2000L,
                avgMedianParallelRate = 97F,
                avgMedianCoverageRate = 16F,
                avgMedianExecs = listOf(
                    TimespanPoint(10L, 1668180871238, 1668280871238),
                    TimespanPoint(20L, 1668280871238, 1668380871238),
                    TimespanPoint(15L, 1668380871238, 1668480871238)
                ),
                diffRate = -10F
            )
        )

        val report = Report("main", "assemble")
        report.modulesExecutionProcessReport = ModulesExecutionProcessReport(data)

        val renderTemplate = "%modules-execution-process-metric%"
        val stage = RenderModulesExecutionProcessReportStage(TowerMockImpl(), report)
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("<tr><th>1</th><th>:woman</th><th>1s</th><th>2s</th><th>97.0%</th><th>16.0%</th><th class=\"green\">-10.0%</th></tr>")
        }
    }

    @Test
    fun `check render when diffRate is zero`() = runBlocking {
        val data = mutableListOf<ModuleExecutionProcess>()

        data.add(
            ModuleExecutionProcess(
                path = ":woman",
                avgMedianExecInMillis = 1000L,
                avgMedianParallelExecInMillis = 2000L,
                avgMedianParallelRate = 97F,
                avgMedianCoverageRate = 16F,
                avgMedianExecs = listOf(
                    TimespanPoint(10L, 1668180871238, 1668280871238),
                    TimespanPoint(20L, 1668280871238, 1668380871238),
                    TimespanPoint(15L, 1668380871238, 1668480871238)
                ),
                diffRate = 0F
            )
        )

        val report = Report("main", "assemble")
        report.modulesExecutionProcessReport = ModulesExecutionProcessReport(data)

        val renderTemplate = "%modules-execution-process-metric%"
        val stage = RenderModulesExecutionProcessReportStage(TowerMockImpl(), report)
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("<tr><th>1</th><th>:woman</th><th>1s</th><th>2s</th><th>97.0%</th><th>16.0%</th><th>Equals</th></tr>")
        }
    }

    @Test
    fun `check render when diffRate is null`() = runBlocking {
        val data = mutableListOf<ModuleExecutionProcess>()

        data.add(
            ModuleExecutionProcess(
                path = ":woman",
                avgMedianExecInMillis = 1000L,
                avgMedianParallelExecInMillis = 2000L,
                avgMedianParallelRate = 97F,
                avgMedianCoverageRate = 16F,
                avgMedianExecs = listOf(
                    TimespanPoint(10L, 1668180871238, 1668280871238),
                    TimespanPoint(20L, 1668280871238, 1668380871238),
                    TimespanPoint(15L, 1668380871238, 1668480871238)
                ),
                diffRate = null
            )
        )

        val report = Report("main", "assemble")
        report.modulesExecutionProcessReport = ModulesExecutionProcessReport(data)

        val renderTemplate = "%modules-execution-process-metric%"
        val stage = RenderModulesExecutionProcessReportStage(TowerMockImpl(), report)
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("<tr><th>1</th><th>:woman</th><th>1s</th><th>2s</th><th>97.0%</th><th>16.0%</th><th>Unknown</th></tr>")
        }
    }

}
