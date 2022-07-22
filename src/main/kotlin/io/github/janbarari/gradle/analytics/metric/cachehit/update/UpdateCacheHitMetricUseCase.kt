package io.github.janbarari.gradle.analytics.metric.cachehit.update

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.CacheHitMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleCacheHit
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.MathUtils

class UpdateCacheHitMetricUseCase(
    private val repo: DatabaseRepository
) : UseCaseNoInput<CacheHitMetric>() {

    override suspend fun execute(): CacheHitMetric {
        val temporaryMetrics = repo.getTemporaryMetrics()

        val hitRatios = mutableListOf<Long>()
        temporaryMetrics.whenEach {
            cacheHitMetric.whenNotNull {
                hitRatios.add(hitRatio)
            }
        }

        val modules = mutableListOf<ModuleCacheHit>()

        temporaryMetrics.last().cacheHitMetric.whenNotNull {
            this.modules.whenEach {
                modules.add(
                    ModuleCacheHit(
                        path = path,
                        hitRatio = getModuleMedianCacheHit(path, temporaryMetrics)
                    )
                )
            }
        }

        println(modules)

        return CacheHitMetric(
            hitRatio = MathUtils.longMedian(hitRatios),
            modules = modules
        )
    }

    private fun getModuleMedianCacheHit(path: String, metrics: List<BuildMetric>): Long {
        val hits = mutableListOf<Long>()
        metrics.whenEach {
            cacheHitMetric.whenNotNull {
                modules.find { it.path == path }
                    .whenNotNull {
                        hits.add(hitRatio)
                    }
            }
        }
        return MathUtils.longMedian(hits)
    }

}
