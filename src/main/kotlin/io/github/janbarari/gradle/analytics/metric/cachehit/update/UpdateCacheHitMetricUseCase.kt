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
package io.github.janbarari.gradle.analytics.metric.cachehit.update

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.CacheHitRateMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.modify
import io.github.janbarari.gradle.utils.MathUtils

class UpdateCacheHitMetricUseCase(
    private val repo: DatabaseRepository
) : UseCaseNoInput<CacheHitRateMetric?>() {

    override suspend fun execute(): CacheHitRateMetric? {
        val temporaryMetrics = repo.getTemporaryMetrics()

        val hitRatios = temporaryMetrics.filter { it.cacheHitRateMetric.isNotNull() }
            .map { ensureNotNull(it.cacheHitRateMetric).rate }

        val modules = temporaryMetrics.last().cacheHitRateMetric?.modules?.modify {
            rate = getModuleMedianCacheHit(path, temporaryMetrics)
        } ?: return null

        return CacheHitRateMetric(
            rate = MathUtils.longMean(hitRatios),
            modules = modules
        )
    }

    private fun getModuleMedianCacheHit(path: String, metrics: List<BuildMetric>): Long {
        val hitRatios = metrics
            .filter {
                it.cacheHitRateMetric.isNotNull()
                        && it.cacheHitRateMetric!!.modules.find { module -> module.path == path }.isNotNull()
            }
            .map {
                it.cacheHitRateMetric!!.modules.find { module -> module.path == path }!!.rate
            }

        return MathUtils.longMean(hitRatios)
    }

}
