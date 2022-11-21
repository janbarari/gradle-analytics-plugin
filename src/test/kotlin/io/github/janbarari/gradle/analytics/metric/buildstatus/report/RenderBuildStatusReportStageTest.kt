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

import io.github.janbarari.gradle.analytics.domain.model.report.BuildStatusReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RenderBuildStatusReportStageTest {

    @Test
    fun `check render when report is null`() = runBlocking {
        val report = Report("main", "assemble")

        val renderTemplate = "%build-status-metric%"
        val stage = RenderBuildStatusReportStage(report)
        val result = stage.process(renderTemplate)

        val expectedAnswer = "<p>Build Status is not available!</p><div class=\"space\"></div>"
        assertEquals(expectedAnswer, result)
    }

    @Test
    fun `check render when report is available`() = runBlocking {
        val report = Report("main", "assemble")
        report.buildStatusReport = BuildStatusReport(
            cumulativeDependencyResolveBySeconds = 4L,
            cumulativeOverallBuildProcessBySeconds = 28,
            avgOverallBuildProcessBySeconds = 25,
            totalBuildProcessCount = 33,
            totalProjectModulesCount = 3,
            cumulativeParallelExecutionBySeconds = 39,
            avgParallelExecutionRate = 45F,
            totalSucceedBuildCount = 33,
            totalFailedBuildCount = 12,
            avgCacheHitRate = 44F,
            avgInitializationProcessByMillis = 130,
            avgConfigurationProcessByMillis = 1900,
            avgExecutionProcessBySeconds = 20
        )

        val renderTemplate = "%build-status-metric%"
        val stage = RenderBuildStatusReportStage(report)
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("<div class=\"table-container basic-info-item\">\n" +
                    "        <table>\n" +
                    "          <tr>\n" +
                    "            <th>\n" +
                    "              <i class=\"bi bi-hourglass-split\"></i><br>\n" +
                    "              Cumulative<br>Overall Build<br>Process\n" +
                    "            </th>\n" +
                    "          </tr>\n" +
                    "          <tr>\n" +
                    "            <th>28s</th>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "      </div>")
        }
        assertTrue {
            result.contains("<div class=\"table-container basic-info-item\">\n" +
                    "        <table>\n" +
                    "          <tr>\n" +
                    "            <th>\n" +
                    "              <i class=\"bi bi-info-circle\"></i><br>\n" +
                    "              Total<br>Build Process<br>Count\n" +
                    "            </th>\n" +
                    "          </tr>\n" +
                    "          <tr>\n" +
                    "            <th>33</th>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "      </div>")
        }
        assertTrue {
            result.contains("<div class=\"table-container basic-info-item\">\n" +
                    "        <table>\n" +
                    "          <tr>\n" +
                    "            <th>\n" +
                    "              <i class=\"bi bi-diagram-3-fill\"></i><br>\n" +
                    "              Total<br>Modules<br>Count\n" +
                    "            </th>\n" +
                    "          </tr>\n" +
                    "          <tr>\n" +
                    "            <th>3</th>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "      </div>")
        }
        assertTrue {
            result.contains("<div class=\"table-container basic-info-item\">\n" +
                    "        <table>\n" +
                    "          <tr>\n" +
                    "            <th>\n" +
                    "              <i class=\"bi bi-shuffle\"></i><br>\n" +
                    "              Cumulative<br>Parallel Exec<br>Duration\n" +
                    "            </th>\n" +
                    "          </tr>\n" +
                    "          <tr>\n" +
                    "            <th>39s</th>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "      </div>")
        }
        assertTrue {
            result.contains("<div class=\"table-container basic-info-item\">\n" +
                    "        <table>\n" +
                    "          <tr>\n" +
                    "            <th >\n" +
                    "              <i class=\"bi bi-shuffle\"></i><br>\n" +
                    "              Average<br>Parallel Exec<br>Rate\n" +
                    "            </th>\n" +
                    "          </tr>\n" +
                    "          <tr>\n" +
                    "            <th>45.0%</th>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "      </div>")
        }
        assertTrue {
            result.contains("<div class=\"table-container basic-info-item\">\n" +
                    "        <table>\n" +
                    "          <tr>\n" +
                    "            <th>\n" +
                    "              <i class=\"bi bi-check-circle\"></i><br>\n" +
                    "              Total<br>Succeed | Failed<br>Builds\n" +
                    "            </th>\n" +
                    "          </tr>\n" +
                    "          <tr>\n" +
                    "            <th>\n" +
                    "              <span class=\"green\">33</span> | <span class=\"red\">12</span>\n" +
                    "            </th>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "      </div>")
        }
        assertTrue {
            result.contains("<div class=\"table-container basic-info-item\">\n" +
                    "        <table>\n" +
                    "          <tr>\n" +
                    "            <th>\n" +
                    "              <i class=\"bi bi-speedometer2\"></i><br>\n" +
                    "              Average<br>Cache Hit<br>Rate\n" +
                    "            </th>\n" +
                    "          </tr>\n" +
                    "          <tr>\n" +
                    "            <th>44.0%</th>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "      </div>")
        }
        assertTrue {
            result.contains("<div class=\"table-container basic-info-item\">\n" +
                    "        <table>\n" +
                    "          <tr>\n" +
                    "            <th>\n" +
                    "              <i class=\"bi bi-cloud-download\"></i><br>\n" +
                    "              Cumulative<br>Dependency<br>Resolve\n" +
                    "            </th>\n" +
                    "          </tr>\n" +
                    "          <tr>\n" +
                    "            <th>4s</th>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "      </div>")
        }
        assertTrue {
            result.contains("<div class=\"table-container basic-info-item\">\n" +
                    "        <table>\n" +
                    "          <tr>\n" +
                    "            <th>\n" +
                    "              <i class=\"bi bi-1-circle\"></i><br>\n" +
                    "              Average<br>Overall Build<br>Process\n" +
                    "            </th>\n" +
                    "          </tr>\n" +
                    "          <tr>\n" +
                    "            <th>25s</th>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "      </div>")
        }
        assertTrue {
            result.contains("<div class=\"table-container basic-info-item\">\n" +
                    "        <table>\n" +
                    "          <tr>\n" +
                    "            <th>\n" +
                    "              <i class=\"bi bi-2-circle\"></i><br>\n" +
                    "              Average<br>Initialization<br>Process\n" +
                    "            </th>\n" +
                    "          </tr>\n" +
                    "          <tr>\n" +
                    "            <th>130ms</th>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "      </div>")
        }
        assertTrue {
            result.contains("<div class=\"table-container basic-info-item\">\n" +
                    "        <table>\n" +
                    "          <tr>\n" +
                    "            <th>\n" +
                    "              <i class=\"bi bi-3-circle\"></i><br>\n" +
                    "              Average<br>Configuration<br>Process\n" +
                    "            </th>\n" +
                    "          </tr>\n" +
                    "          <tr>\n" +
                    "            <th>1900ms</th>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "      </div>")
        }
        assertTrue {
            result.contains("<div class=\"table-container basic-info-item\">\n" +
                    "        <table>\n" +
                    "          <tr>\n" +
                    "            <th>\n" +
                    "              <i class=\"bi bi-4-circle\"></i><br>\n" +
                    "              Average<br>Execution<br>Process\n" +
                    "            </th>\n" +
                    "          </tr>\n" +
                    "          <tr>\n" +
                    "            <th>20s</th>\n" +
                    "          </tr>\n" +
                    "        </table>\n" +
                    "      </div>")
        }

    }

}
