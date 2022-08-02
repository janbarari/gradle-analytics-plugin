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
package io.github.janbarari.gradle.analytics.metric.cachehit.create

import io.github.janbarari.gradle.analytics.domain.model.metric.CacheHitMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleCacheHit
import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.extension.toPercentageOf
import io.github.janbarari.gradle.extension.whenEach

class CreateCacheHitMetricUseCase: UseCase<Pair<List<ModulePath>, Collection<TaskInfo>>, CacheHitMetric>() {

    override suspend fun execute(input: Pair<List<ModulePath>, Collection<TaskInfo>>): CacheHitMetric {
        val modulesPath = input.first
        val executedTasks = input.second

        var cachedTasksCount = 0
        executedTasks.whenEach {
            if (!isSkipped && (isUpToDate || isFromCache)) {
                cachedTasksCount++
            }
        }
        val overallCacheHitRatio = cachedTasksCount.toPercentageOf(executedTasks.filter { it.isSkipped.not() }.size)

        val modulesCacheHit = mutableListOf<ModuleCacheHit>()
        modulesPath.whenEach {
            var moduleCachedTasksCount = 0
            var moduleTasksCount = 0
            executedTasks.filter { it.path.startsWith(path) }
                .whenEach {
                    moduleTasksCount++
                    if (!isSkipped && (isUpToDate || isFromCache)) {
                        moduleCachedTasksCount++
                    }
                }
            val moduleCacheHitRatio = moduleCachedTasksCount.toPercentageOf(moduleTasksCount)
            modulesCacheHit.add(
                ModuleCacheHit(
                    path = path,
                    rate = moduleCacheHitRatio.toLong()
                )
            )
        }
        return CacheHitMetric(overallCacheHitRatio.toLong(), modulesCacheHit)
    }

}
