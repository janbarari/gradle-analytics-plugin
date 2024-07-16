/**
 * MIT License
 * Copyright (c) 2024 Mehdi Janbarari (@janbarari)
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
package io.github.janbarari.gradle.analytics.metric.modulescrashcount.create

import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.Module
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesCrashCountMetric
import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.logger.Tower

class CreateModulesCrashCountMetricUseCase(
    private val tower: Tower,
    private val modules: Set<Module>
) : UseCase<BuildInfo, ModulesCrashCountMetric>() {

    companion object {
        private val clazz = CreateModulesCrashCountMetricUseCase::class.java
    }

    override suspend fun execute(input: BuildInfo): ModulesCrashCountMetric {
        tower.i(clazz, "execute()")
        val modules = mutableListOf<ModulesCrashCountMetric.ModuleCrash>()

        if (!input.isSuccessful && input.failure.isNotNull()) {
            val findFirstFailedTask = input.executedTasks.sortedBy { it.startedAt }.firstOrNull { !it.isSuccessful }

            findFirstFailedTask.whenNotNull {
                val module = this@CreateModulesCrashCountMetricUseCase.modules.firstOrNull {
                    it.path == getModule()
                }
                module.whenNotNull {
                    modules.add(
                        ModulesCrashCountMetric.ModuleCrash(
                            path = path,
                            totalCrashes = 1
                        )
                    )
                }
            }

        }

        return ModulesCrashCountMetric(
            modules = modules
        )
    }

}
