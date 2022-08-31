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
package io.github.janbarari.gradle.analytics.metric.modulestimeline.create

import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleTimeline
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesTimelineMetric
import io.github.janbarari.gradle.core.UseCase

class CreateModulesTimelineMetricUseCase(
    private val modulesPath: List<ModulePath>
): UseCase<BuildInfo, ModulesTimelineMetric>() {

    override suspend fun execute(buildInfo: BuildInfo): ModulesTimelineMetric {
        val start = buildInfo.executedTasks.minOfOrNull { it.startedAt } ?: 0
        val end = buildInfo.executedTasks.maxOfOrNull { it.finishedAt } ?: 0

        val result = modulesPath.map { module ->
            ModuleTimeline(
                path = module.path,
                timelines = buildInfo.executedTasks.filter { it.path.startsWith(module.path) }
                    .map {
                        ModuleTimeline.Timeline(
                            start = it.startedAt,
                            end = it.finishedAt,
                            isCached = it.isFromCache || it.isUpToDate
                        )
                    }
            )
        }

        return ModulesTimelineMetric(
            modules = result,
            start = start,
            end = end
        )
    }

}
