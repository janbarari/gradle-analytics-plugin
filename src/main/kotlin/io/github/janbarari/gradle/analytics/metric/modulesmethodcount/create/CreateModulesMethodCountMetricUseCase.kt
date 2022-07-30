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
package io.github.janbarari.gradle.analytics.metric.modulesmethodcount.create

import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleMethodCount
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesMethodCountMetric
import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.extension.isJavaFile
import io.github.janbarari.gradle.extension.isKotlinFile
import io.github.janbarari.gradle.extension.readText
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.utils.FileUtils
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.util.*

class CreateModulesMethodCountMetricUseCase : UseCase<List<ModulePath>, ModulesMethodCountMetric>() {

    private val commentRegex = """(//.*)|(\/\*[^/*]*(?:(?!\/\*|\*\/)[/*][^/*]*)*\*\/)""".toRegex()

    private val javaModifiers = "public|private|protected|static|final|native|synchronized|abstract|transient|"
    private val javaMethodRegex =
        """($javaModifiers)+[\w\<\>\[\]\,\s]*\s*(\w+) *\([^\)]*\) *(\{|[^;])""".toRegex()

    private val kotlinMethodRegex = """((fun)+[\\${'$'}\w\<\>\w\s\[\]]*\s+\w.*\([^\)]*\) *(.*) *(\{|\=))""".toRegex()
    private val kotlinConstructorRegex =
        """(.*class[\\${'$'}\w\<\>\w\s\[\]]*\s+\w.*\([^\)]*\)|.*constructor.*\([^\)]*\))|((\sinit) *(\{|\=))"""
            .toRegex()

    override suspend fun execute(modulesPath: List<ModulePath>): ModulesMethodCountMetric {
        val modulesProperties = Collections.synchronizedList(mutableListOf<ModuleMethodCount>())
        withContext(dispatcher) {
            val defers = mutableListOf<Deferred<Boolean>>()
            modulesPath.whenEach {
                defers.add(async {
                    modulesProperties.add(
                        ModuleMethodCount(
                            path = path,
                            value = getModuleMethodCount(absoluteDir)
                        )
                    )
                })
            }
            defers.awaitAll()
        }
        return ModulesMethodCountMetric(modules = modulesProperties)
    }

    private fun getModuleMethodCount(directory: String): Int {
        val sourcePaths = FileUtils.getModuleSources(directory)
        var result = 0

        sourcePaths.whenEach {
            if (isKotlinFile()) {
                val content = readText()
                val removedComments = content.replace(commentRegex, "")
                result += kotlinMethodRegex.findAll(removedComments).count()
                result += kotlinConstructorRegex.findAll(removedComments).count()
            }
            if (isJavaFile()) {
                val content = readText()
                val removedComments = content.replace(commentRegex, "")
                result += javaMethodRegex.findAll(removedComments).count()
            }
        }

        return result
    }

}
