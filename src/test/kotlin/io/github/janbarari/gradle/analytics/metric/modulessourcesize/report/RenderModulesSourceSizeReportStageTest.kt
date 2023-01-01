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
package io.github.janbarari.gradle.analytics.metric.modulessourcesize.report

import io.github.janbarari.gradle.TowerMockImpl
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesSourceSizeReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RenderModulesSourceSizeReportStageTest {

    @Test
    fun `check render when report is null`() = runBlocking {
        val report = Report("main", "assemble")

        val renderTemplate = "%modules-source-size-metric%"
        val stage = RenderModulesSourceSizeReportStage(TowerMockImpl(), report)
        val result = stage.process(renderTemplate)

        val expectedAnswer = "<p>Modules Source Size is not available!</p><div class=\"space\"></div>"
        assertEquals(expectedAnswer, result)
    }

    @Test
    fun `check render when total diff rate is positive`() = runBlocking {
        val report = Report("main", "assemble")
        report.modulesSourceSizeReport = ModulesSourceSizeReport(
            listOf(
                ModulesSourceSizeReport.ModuleSourceSize(
                    path = ":woman",
                    sizeInKb = 56,
                    coverageRate = 14F,
                    diffRate = 12F
                ),
                ModulesSourceSizeReport.ModuleSourceSize(
                    path = ":life",
                    sizeInKb = 100,
                    coverageRate = 23F,
                    diffRate = 0F
                ),
                ModulesSourceSizeReport.ModuleSourceSize(
                    path = ":freedom",
                    sizeInKb = 112,
                    coverageRate = 65F,
                    diffRate = -14F
                )
            ),
            totalSourceSizeInKb = 1024,
            totalDiffRate = 12F
        )

        val renderTemplate = "%modules-source-size-metric%"
        val stage = RenderModulesSourceSizeReportStage(TowerMockImpl(), report)
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("<tr>\n" +
                    "                        <td colspan=\"2\">Total</td>\n" +
                    "                        <td>1024kb</td>\n" +
                    "                        <td>Σ</td>\n" +
                    "                        <td>+12.0%</td>\n" +
                    "                    </tr>")
        }
        assertTrue {
            result.contains("labels: [\":woman\", \":life\", \":freedom\"]")
        }
        assertTrue {
            result.contains("var _data = [56, 100, 112];")
        }
        assertTrue {
            result.contains("<td>1</td>\n" +
                    "    <td>:woman</td>\n" +
                    "    <td>56kb</td>\n" +
                    "    <td>14.0%</td>\n" +
                    "    <td>+12.0%</td>")
        }
        assertTrue {
            result.contains("<td>2</td>\n" +
                    "    <td>:life</td>\n" +
                    "    <td>100kb</td>\n" +
                    "    <td>23.0%</td>\n" +
                    "    <td>Equals</td>")
        }
        assertTrue {
            result.contains(
                "<td>3</td>\n" +
                        "    <td>:freedom</td>\n" +
                        "    <td>112kb</td>\n" +
                        "    <td>65.0%</td>\n" +
                        "    <td>-14.0%</td>"
            )
        }
    }

    @Test
    fun `check render when total diff rate is zero`() = runBlocking {
        val report = Report("main", "assemble")
        report.modulesSourceSizeReport = ModulesSourceSizeReport(
            listOf(
                ModulesSourceSizeReport.ModuleSourceSize(
                    path = ":woman",
                    sizeInKb = 56,
                    coverageRate = 14F,
                    diffRate = 12F
                ),
                ModulesSourceSizeReport.ModuleSourceSize(
                    path = ":life",
                    sizeInKb = 100,
                    coverageRate = 23F,
                    diffRate = 0F
                ),
                ModulesSourceSizeReport.ModuleSourceSize(
                    path = ":freedom",
                    sizeInKb = 112,
                    coverageRate = 65F,
                    diffRate = -14F
                )
            ),
            totalSourceSizeInKb = 1024,
            totalDiffRate = 0F
        )

        val renderTemplate = "%modules-source-size-metric%"
        val stage = RenderModulesSourceSizeReportStage(TowerMockImpl(), report)
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("<tr>\n" +
                    "                        <td colspan=\"2\">Total</td>\n" +
                    "                        <td>1024kb</td>\n" +
                    "                        <td>Σ</td>\n" +
                    "                        <td>Equals</td>\n" +
                    "                    </tr>")
        }
        assertTrue {
            result.contains("labels: [\":woman\", \":life\", \":freedom\"]")
        }
        assertTrue {
            result.contains("var _data = [56, 100, 112];")
        }
        assertTrue {
            result.contains("<td>1</td>\n" +
                    "    <td>:woman</td>\n" +
                    "    <td>56kb</td>\n" +
                    "    <td>14.0%</td>\n" +
                    "    <td>+12.0%</td>")
        }
        assertTrue {
            result.contains("<td>2</td>\n" +
                    "    <td>:life</td>\n" +
                    "    <td>100kb</td>\n" +
                    "    <td>23.0%</td>\n" +
                    "    <td>Equals</td>")
        }
        assertTrue {
            result.contains(
                "<td>3</td>\n" +
                        "    <td>:freedom</td>\n" +
                        "    <td>112kb</td>\n" +
                        "    <td>65.0%</td>\n" +
                        "    <td>-14.0%</td>"
            )
        }
    }

    @Test
    fun `check render when total diff rate is negative`() = runBlocking {
        val report = Report("main", "assemble")
        report.modulesSourceSizeReport = ModulesSourceSizeReport(
            listOf(
                ModulesSourceSizeReport.ModuleSourceSize(
                    path = ":woman",
                    sizeInKb = 56,
                    coverageRate = 14F,
                    diffRate = 12F
                ),
                ModulesSourceSizeReport.ModuleSourceSize(
                    path = ":life",
                    sizeInKb = 100,
                    coverageRate = 23F,
                    diffRate = 0F
                ),
                ModulesSourceSizeReport.ModuleSourceSize(
                    path = ":freedom",
                    sizeInKb = 112,
                    coverageRate = 65F,
                    diffRate = -14F
                )
            ),
            totalSourceSizeInKb = 1024,
            totalDiffRate = -25F
        )

        val renderTemplate = "%modules-source-size-metric%"
        val stage = RenderModulesSourceSizeReportStage(TowerMockImpl(), report)
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("<tr>\n" +
                    "                        <td colspan=\"2\">Total</td>\n" +
                    "                        <td>1024kb</td>\n" +
                    "                        <td>Σ</td>\n" +
                    "                        <td>-25.0%</td>\n" +
                    "                    </tr>")
        }
        assertTrue {
            result.contains("labels: [\":woman\", \":life\", \":freedom\"]")
        }
        assertTrue {
            result.contains("var _data = [56, 100, 112];")
        }
        assertTrue {
            result.contains("<td>1</td>\n" +
                    "    <td>:woman</td>\n" +
                    "    <td>56kb</td>\n" +
                    "    <td>14.0%</td>\n" +
                    "    <td>+12.0%</td>")
        }
        assertTrue {
            result.contains("<td>2</td>\n" +
                    "    <td>:life</td>\n" +
                    "    <td>100kb</td>\n" +
                    "    <td>23.0%</td>\n" +
                    "    <td>Equals</td>")
        }
        assertTrue {
            result.contains(
                "<td>3</td>\n" +
                        "    <td>:freedom</td>\n" +
                        "    <td>112kb</td>\n" +
                        "    <td>65.0%</td>\n" +
                        "    <td>-14.0%</td>"
            )
        }
    }

}
