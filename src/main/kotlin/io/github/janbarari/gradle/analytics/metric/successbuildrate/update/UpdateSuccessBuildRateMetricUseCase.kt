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
package io.github.janbarari.gradle.analytics.metric.successbuildrate.update

import io.github.janbarari.gradle.analytics.domain.model.metric.SuccessBuildRateMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.toPercentageOf
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull

class UpdateSuccessBuildRateMetricUseCase(
    private val repo: DatabaseRepository
) : UseCaseNoInput<SuccessBuildRateMetric>() {

    override suspend fun execute(): SuccessBuildRateMetric {
        var meanSuccesses = 0
        var meanFailures = 0
        var medianSuccesses = 0
        var medianFailures = 0
        var successesCount = 0
        var failsCount = 0

        repo.getTemporaryMetrics().whenEach {
            successBuildRateMetric.whenNotNull {
                when (meanRate) {
                    0F -> meanFailures++
                    100F -> meanSuccesses++
                }
                when (medianRate) {
                    0F -> medianFailures++
                    100F -> medianSuccesses++
                }
                successesCount += successes
                failsCount += fails
            }
        }

        val meanTotalBuildCount = meanFailures + meanSuccesses
        val medianTotalBuildCount = medianFailures + medianSuccesses

        return SuccessBuildRateMetric(
            medianRate = medianSuccesses.toPercentageOf(medianTotalBuildCount),
            meanRate = meanSuccesses.toPercentageOf(meanTotalBuildCount),
            successes = successesCount,
            fails = failsCount
        )
    }

}
