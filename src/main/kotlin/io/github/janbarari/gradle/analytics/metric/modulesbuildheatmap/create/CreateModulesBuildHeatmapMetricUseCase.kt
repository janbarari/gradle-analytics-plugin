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
package io.github.janbarari.gradle.analytics.metric.modulesbuildheatmap.create

import io.github.janbarari.gradle.analytics.domain.model.Module
import io.github.janbarari.gradle.analytics.domain.model.ModulesDependencyGraph
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleBuildHeatmap
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesBuildHeatmapMetric
import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.core.UseCaseNoInput

class CreateModulesBuildHeatmapMetricUseCase(
    private val modules: List<Module>,
    private val modulesDependencyGraph: ModulesDependencyGraph
): UseCaseNoInput<ModulesBuildHeatmapMetric>() {

    override suspend fun execute(): ModulesBuildHeatmapMetric {
        val result = mutableListOf<ModuleBuildHeatmap>()

        modules.forEach { module ->
            val dependantModulesCount = modulesDependencyGraph.dependencies.filter { it.dependency == module.path }.size
            result.add(
                ModuleBuildHeatmap(
                    path = module.path,
                    dependantModulesCount = dependantModulesCount
                )
            )
        }

        return ModulesBuildHeatmapMetric(
            modules = result
        )
    }

}
