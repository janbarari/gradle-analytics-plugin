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
package io.github.janbarari.gradle.analytics.metric.execution.update

import io.github.janbarari.gradle.analytics.domain.model.metric.ExecutionProcessMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.isBiggerEquals
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.extension.whenTrue
import io.github.janbarari.gradle.utils.MathUtils

class UpdateExecutionMetricUseCase(
    private val repo: DatabaseRepository
): UseCaseNoInput<ExecutionProcessMetric>() {

    companion object {
        private const val SKIP_THRESHOLD_IN_MS = 50L
    }

    override suspend fun execute(): ExecutionProcessMetric {
        val durations = arrayListOf<Long>()
        repo.getTemporaryMetrics().whenEach {
            executionProcessMetric.whenNotNull {
                // In order to have accurate metric, don't add metric value in Median dataset if it's under 50 milliseconds.
                median.isBiggerEquals(SKIP_THRESHOLD_IN_MS)
                    .whenTrue {
                        durations.add(median)
                    }
            }
        }

        return ExecutionProcessMetric(
            median = MathUtils.longMedian(durations)
        )
    }

}
