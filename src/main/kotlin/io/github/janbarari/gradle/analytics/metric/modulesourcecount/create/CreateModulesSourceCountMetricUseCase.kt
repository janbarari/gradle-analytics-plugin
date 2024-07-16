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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.metric.modulesourcecount.create

import io.github.janbarari.gradle.analytics.domain.model.Module
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleSourceCount
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesSourceCountMetric
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.utils.FileUtils
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.util.*

class CreateModulesSourceCountMetricUseCase(
    private val tower: Tower,
    private val modules: Set<Module>
) : UseCaseNoInput<ModulesSourceCountMetric>() {

    companion object {
        private val clazz = CreateModulesSourceCountMetricUseCase::class.java
    }

    override suspend fun execute(): ModulesSourceCountMetric {
        tower.i(clazz, "execute()")
        val result = Collections.synchronizedList(mutableListOf<ModuleSourceCount>())
        withContext(dispatcher) {
            val defers = mutableListOf<Deferred<Boolean>>()
            modules.whenEach {
                defers.add(async {
                    result.add(
                        ModuleSourceCount(
                            path = path,
                            value = FileUtils.getModuleSources(absoluteDir).size
                        )
                    )
                })
            }
            defers.awaitAll()
        }
        return ModulesSourceCountMetric(modules = result)
    }

}
