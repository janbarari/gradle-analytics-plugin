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
package io.github.janbarari.gradle.analytics.metric.modulesbuildheatmap.render

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.report.ModuleBuildHeatmap
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesBuildHeatmapReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.MathUtils

class CreateModulesBuildHeatmapReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    override suspend fun process(input: Report): Report {
        val temp = mutableListOf<ModuleBuildHeatmap>()

        metrics.last().modulesBuildHeatmap.whenNotNull {
            modules.forEach { module ->
                temp.add(
                    ModuleBuildHeatmap(
                        path = module.path,
                        dependantModulesCount = module.dependantModulesCount,
                        avgMedianCacheHit = getAvgMeanCacheHit(module.path),
                        totalBuildCount = getModuleTotalBuildCount(module.path)
                    )
                )
            }
        }

        return input.apply {
            modulesBuildHeatmapReport = ModulesBuildHeatmapReport(
                modules = temp
            )
        }
    }

    fun getAvgMeanCacheHit(path: String): Long {
        val hits = mutableListOf<Long>()
        metrics.filter {
            it.cacheHitMetric.isNotNull()
        }.whenEach {
            cacheHitMetric!!.modules.filter { moduleCacheHit ->
                moduleCacheHit.path == path
            }.whenEach {
                hits.add(rate)
            }
        }
        return MathUtils.longMean(hits)
    }

    fun getModuleTotalBuildCount(path: String): Int {
        return metrics.filter {
            it.modulesBuildHeatmap.isNotNull() && it.modulesBuildHeatmap!!.modules.any { module -> module.path == path }
        }.size
    }

}
