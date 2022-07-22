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
            if (isUpToDate || isFromCache) {
                cachedTasksCount++
            }
        }
        val overallCacheHitRatio = cachedTasksCount.toPercentageOf(executedTasks.size)

        val modulesCacheHit = mutableListOf<ModuleCacheHit>()
        modulesPath.whenEach {
            var moduleCachedTasksCount = 0
            var moduleTasksCount = 0
            executedTasks.filter { it.path.startsWith(path) }
                .whenEach {
                    moduleTasksCount++
                    if (isUpToDate || isFromCache) {
                        moduleCachedTasksCount++
                    }
                }
            val moduleCacheHitRatio = moduleCachedTasksCount.toPercentageOf(moduleTasksCount)
            modulesCacheHit.add(
                ModuleCacheHit(
                    path = path,
                    hitRatio = moduleCacheHitRatio.toLong()
                )
            )
        }
        return CacheHitMetric(overallCacheHitRatio.toLong(), modulesCacheHit)
    }

}
