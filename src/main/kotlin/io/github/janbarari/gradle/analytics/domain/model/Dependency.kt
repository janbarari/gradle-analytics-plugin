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
package io.github.janbarari.gradle.analytics.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.janbarari.gradle.ExcludeJacocoGenerated
import org.gradle.api.Project

@ExcludeJacocoGenerated
@JsonClass(generateAdapter = true)
data class Dependency(
    @Json(name = "name")
    val name: String,
    @Json(name = "module_name")
    val moduleName: String,
    @Json(name = "module_group")
    val moduleGroup: String,
    @Json(name = "module_version")
    val moduleVersion: String,
    @Json(name = "size_by_kb")
    val sizeByKb: Long
): java.io.Serializable {

    companion object {

        fun Project.getThirdPartyDependencies(): List<Dependency> {
            return subprojects.flatMap { project ->
                project.configurations.filter {
                    it.isCanBeResolved && it.name.contains("compileClassPath", ignoreCase = true)
                }.flatMap { configuration ->
                    configuration.resolvedConfiguration.firstLevelModuleDependencies.filter { resolvedDependency ->
                        !resolvedDependency.moduleVersion.equals("unspecified", true)
                    }.map { it }
                }
            }.toSet()
                .map {
                    Dependency(
                        name = it.name,
                        moduleName = it.moduleName,
                        moduleGroup = it.moduleGroup,
                        moduleVersion = it.moduleVersion,
                        sizeByKb = it.moduleArtifacts.sumOf { artifact -> artifact.file.length() / 1024L }
                    )
                }
        }

    }

}
