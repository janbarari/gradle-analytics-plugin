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
package io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.update

import io.github.janbarari.gradle.analytics.domain.model.Module
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleExecutionProcess
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesExecutionProcessMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.MathUtils

class UpdateModulesExecutionProcessMetricUseCase(
    private val repo: DatabaseRepository,
    private val modules: List<Module>
): UseCaseNoInput<ModulesExecutionProcessMetric>() {

    override suspend fun execute(): ModulesExecutionProcessMetric {
        val modulesMedianExecutionProcess = modules.map {
            calculateMedianModuleExecutionProcess(
                modulePath = it.path,
                metrics = repo.getTemporaryMetrics()
            )
        }

        return ModulesExecutionProcessMetric(
            modules = modulesMedianExecutionProcess
        )
    }

    private fun calculateMedianModuleExecutionProcess(modulePath: String, metrics: List<BuildMetric>): ModuleExecutionProcess {
        val medianDurations = mutableListOf<Long>()
        val medianParallelDurations = mutableListOf<Long>()
        val medianParallelRates = mutableListOf<Float>()
        val medianCoverages = mutableListOf<Float>()

        metrics.whenEach {
            modulesExecutionProcessMetric.whenNotNull {
                modules.find { it.path == modulePath }.whenNotNull {
                    medianDurations.add(duration)
                    medianParallelDurations.add(parallelDuration)
                    medianParallelRates.add(parallelRate)
                    medianCoverages.add(coverage)
                }
            }
        }

        return ModuleExecutionProcess(
            path = modulePath,
            duration = MathUtils.longMedian(medianDurations),
            parallelDuration = MathUtils.longMedian(medianParallelDurations),
            parallelRate = MathUtils.floatMedian(medianParallelRates),
            coverage = MathUtils.floatMedian(medianCoverages)
        )
    }

}
