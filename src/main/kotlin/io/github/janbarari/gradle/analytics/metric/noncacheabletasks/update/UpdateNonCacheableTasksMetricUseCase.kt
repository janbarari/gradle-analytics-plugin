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
package io.github.janbarari.gradle.analytics.metric.noncacheabletasks.update

import io.github.janbarari.gradle.analytics.domain.model.metric.NonCacheableTasksMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.modify
import io.github.janbarari.gradle.utils.MathUtils

class UpdateNonCacheableTasksMetricUseCase(
    private val repo: DatabaseRepository
) : UseCaseNoInput<NonCacheableTasksMetric>() {

    override suspend fun execute(): NonCacheableTasksMetric {
        val tasks = repo.getTemporaryMetrics().last().nonCacheableTasksMetric!!.tasks
            .modify {
                val medianValue = repo.getTemporaryMetrics()
                    .filter { it.nonCacheableTasksMetric.isNotNull() }
                    .flatMap {
                        it.nonCacheableTasksMetric!!.tasks
                            .filter { it.path == path }
                            .map { it.avgExecutionDurationInMillis }
                    }
                avgExecutionDurationInMillis = MathUtils.longMedian(medianValue)
            }

        return NonCacheableTasksMetric(
            tasks = tasks
        )
    }

}
