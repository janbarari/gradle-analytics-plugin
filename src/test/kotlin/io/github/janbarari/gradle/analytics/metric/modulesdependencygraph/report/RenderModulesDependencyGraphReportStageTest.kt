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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.metric.modulesdependencygraph.report

import com.squareup.moshi.Moshi
import io.github.janbarari.gradle.TowerMockImpl
import io.github.janbarari.gradle.analytics.domain.model.ModuleDependency
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesDependencyGraphReport
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesDependencyGraphReportJsonAdapter
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RenderModulesDependencyGraphReportStageTest {

    @Test
    fun `check render when report is null`() = runBlocking {
        val report = Report("main", "assemble")

        val renderTemplate = "%modules-dependency-graph-metric%"
        val stage = RenderModulesDependencyGraphReportStage(
            TowerMockImpl(),
            ModulesDependencyGraphReportJsonAdapter(Moshi.Builder().build()),
            report,
            "./build/",
            "PROJECT_NAME",
            setOf()
        )
        val result = stage.process(renderTemplate)

        val expectedAnswer = "<p>Modules Dependency Graph is not available!</p><div class=\"space\"></div>"
        assertEquals(expectedAnswer, result)
    }

    @Test
    fun `check render when report is available`() = runBlocking {
        val report = Report("main", "assemble")
        report.modulesDependencyGraphReport = ModulesDependencyGraphReport(
            dependencies = listOf(
                ModuleDependency(
                    path = ":freedom",
                    configuration = "implementation",
                    dependency = ":life"
                ),
                ModuleDependency(
                    path = ":life",
                    configuration = "implementation",
                    dependency = ":woman"
                ),
                ModuleDependency(
                    path = ":woman",
                    configuration = "implementation",
                    dependency = ""
                )
            ),
            setOf(":woman", ":life", ":freedom")
        )

        val renderTemplate = "%modules-dependency-graph-metric%"
        val stage = RenderModulesDependencyGraphReportStage(
            TowerMockImpl(),
            ModulesDependencyGraphReportJsonAdapter(Moshi.Builder().build()),
            report,
            "./build/",
            "PROJECT_NAME",
            setOf()
        )
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("<a class=\"center-text\" href=\"modules-dependency-graph.html\" target=\"_blank\"><i class=\"bi bi-box-arrow-up-right\"></i> Show Graph</a>")
        }
    }

}
