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
package io.github.janbarari.gradle.analytics.metric.modulesourcecount.create

import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleSourceCount
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesSourceCountMetric
import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.extension.whenEach
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Collectors
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.pathString

class CreateModulesSourceCountMetricUseCase : UseCase<List<ModulePath>, ModulesSourceCountMetric>() {

    override suspend fun execute(modulesPath: List<ModulePath>): ModulesSourceCountMetric {
        val result = Collections.synchronizedList(mutableListOf<ModuleSourceCount>())
        withContext(dispatcher) {
            val defers = mutableListOf<Deferred<Boolean>>()
            modulesPath.whenEach {
                defers.add(async {
                    result.add(
                        ModuleSourceCount(
                            path = path,
                            value = getModuleSources(absoluteDir).size
                        )
                    )
                })
            }
            defers.awaitAll()
        }
        return ModulesSourceCountMetric(modules = result)
    }

    fun getModuleSources(directory: String): List<Path> {
        var sourcePaths: List<Path>
        Files.walk(Path(directory)).use { stream ->
            sourcePaths = stream.map { obj: Path -> obj.normalize() }
                .filter(this::isSourcePath)
                .collect(Collectors.toList())
        }
        return sourcePaths
    }

    fun isSourcePath(path: Path): Boolean {
        return (path.pathString.contains("src/main/java") || path.pathString.contains("src/main/kotlin"))
                && (path.extension == "kt" || path.extension == "java")
                && Files.isRegularFile(path)
    }

}
