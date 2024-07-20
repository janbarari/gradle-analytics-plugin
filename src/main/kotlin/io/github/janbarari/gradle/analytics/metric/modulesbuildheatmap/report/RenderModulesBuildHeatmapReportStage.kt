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
package io.github.janbarari.gradle.analytics.metric.modulesbuildheatmap.report

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.SuspendStage
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.toArrayRender
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.utils.HtmlUtils
import io.github.janbarari.gradle.utils.MathUtils

class RenderModulesBuildHeatmapReportStage(
    private val tower: Tower,
    private val report: Report
) : SuspendStage<String, String> {

    companion object {
        private const val MODULES_BUILD_HEATMAP_TEMPLATE_ID = "%modules-build-heatmap-metric%"
        private const val MODULES_BUILD_HEATMAP_TEMPLATE_FILE_NAME = "modules-build-heatmap-template"
        private val clazz = RenderModulesBuildHeatmapReportStage::class.java
    }

    override suspend fun process(input: String): String {
        tower.i(clazz, "process()")
        if (report.modulesBuildHeatmapReport.isNull())
            return input.replace(MODULES_BUILD_HEATMAP_TEMPLATE_ID, getEmptyRender())

        return input.replace(MODULES_BUILD_HEATMAP_TEMPLATE_ID, getMetricRender())
    }

    fun getEmptyRender(): String {
        return HtmlUtils.renderMessage("Modules Build Heatmap is not available!")
    }

    fun getMetricRender(): String {
        var renderedTemplate = HtmlUtils.getTemplate(MODULES_BUILD_HEATMAP_TEMPLATE_FILE_NAME)
        report.modulesBuildHeatmapReport.whenNotNull {

            val labels = mutableListOf<String>()
            val data = mutableListOf<Long>()
            val colors = mutableListOf<String>()

            modules.sortedByDescending { it.dependantModulesCount }.forEach { module ->
                labels.add("${module.path} | ${module.dependantModulesCount}D")
                colors.add(getColor(module.dependantModulesCount))
                data.add(MathUtils.deductWithPercentage(module.totalBuildCount.toLong(), module.avgMedianCacheHit.toInt()))
            }

            val chartHeight = modules.size * 36

            renderedTemplate = renderedTemplate
                .replace("%labels%", labels.toArrayRender())
                .replace("%data%", data.toString())
                .replace("%colors%", colors.toArrayRender())
                .replace("%chart-height%", "${chartHeight}px")
        }
        return renderedTemplate
    }

    fun getColor(dependantModulesCount: Int): String {
        return if (dependantModulesCount >= 12) "#d73027"
        else if (dependantModulesCount in 8..11) "#fdae61"
        else if (dependantModulesCount in 4..7) "#ffffbf"
        else if (dependantModulesCount in 1..3) "#abd9e9"
        else "#4575b4"
    }
}
