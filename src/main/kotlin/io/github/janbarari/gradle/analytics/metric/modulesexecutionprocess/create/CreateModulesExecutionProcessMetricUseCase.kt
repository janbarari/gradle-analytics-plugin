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
package io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.create

import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.Module
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleExecutionProcess
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesExecutionProcessMetric
import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.extension.toPercentageOf
import io.github.janbarari.gradle.extension.whenEach

class CreateModulesExecutionProcessMetricUseCase(
    private val modules: Set<Module>
): UseCase<BuildInfo, ModulesExecutionProcessMetric>() {

    override suspend fun execute(input: BuildInfo): ModulesExecutionProcessMetric {
        val moduleExecutionProcesses = mutableListOf<ModuleExecutionProcess>()

        modules.whenEach {
            val tasks = input.executedTasks.filter { it.path.startsWith(path) }

            val moduleParallelExecInMillis = tasks.sumOf { it.getDurationInMillis() }

            val moduleNonParallelExecInMillis = input.calculateNonParallelExecutionInMillis(tasks)

            val moduleParallelRate = (moduleParallelExecInMillis - moduleNonParallelExecInMillis)
                .toPercentageOf(moduleNonParallelExecInMillis)

            val overallDuration = input.getExecutionDuration().toMillis()
            val moduleCoverageRate = moduleNonParallelExecInMillis.toPercentageOf(overallDuration)

            moduleExecutionProcesses.add(
                ModuleExecutionProcess(
                    path = path,
                    medianExecInMillis = moduleNonParallelExecInMillis,
                    medianParallelExecInMillis = moduleParallelExecInMillis,
                    parallelRate = moduleParallelRate,
                    coverageRate = moduleCoverageRate
                )
            )
        }

        return ModulesExecutionProcessMetric(
            modules = moduleExecutionProcesses
        )
    }

}
