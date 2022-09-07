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
package io.github.janbarari.gradle.analytics.metric.modulessourcesize.create

import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesSourceSizeMetric
import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.utils.FileUtils
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.util.*

class CreateModulesSourceSizeMetricUseCase : UseCase<List<ModulePath>, ModulesSourceSizeMetric>() {

    override suspend fun execute(modulesPath: List<ModulePath>): ModulesSourceSizeMetric {
        val modulesProperties = Collections.synchronizedList(mutableListOf<ModulesSourceSizeMetric.ModuleSourceSize>())
        withContext(dispatcher) {
            val defers = mutableListOf<Deferred<Boolean>>()
            modulesPath.whenEach {
                defers.add(async {
                    modulesProperties.add(
                        ModulesSourceSizeMetric.ModuleSourceSize(
                            path = path,
                            sizeInKb = getModuleSourceSize(absoluteDir)
                        )
                    )
                })
            }
            defers.awaitAll()
        }
        return ModulesSourceSizeMetric(modules = modulesProperties)
    }

    private fun getModuleSourceSize(directory: String): Long {
        return FileUtils
            .getModuleSources(directory)
            .sumOf { it.toFile().length() / 1024L }
    }

}
