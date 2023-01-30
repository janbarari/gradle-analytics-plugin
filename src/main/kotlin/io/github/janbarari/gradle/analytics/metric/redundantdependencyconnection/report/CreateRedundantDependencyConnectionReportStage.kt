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
package io.github.janbarari.gradle.analytics.metric.redundantdependencyconnection.report

import io.github.janbarari.gradle.analytics.domain.model.ModuleDependency
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.report.RedundantDependency
import io.github.janbarari.gradle.analytics.domain.model.report.RedundantDependencyGraphReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.core.SuspendStage
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.logger.Tower

class CreateRedundantDependencyConnectionReportStage(
    private val tower: Tower,
    private val metrics: List<BuildMetric>
) : SuspendStage<Report, Report> {

    companion object {
        private val clazz = CreateRedundantDependencyConnectionReportStage::class.java
    }

    override suspend fun process(input: Report): Report {
        tower.i(clazz, "process()")
        return input.apply {
            metrics.lastOrNull()?.whenNotNull {
                modulesDependencyGraphMetric?.dependencies.whenNotNull {
                    redundantDependencyGraphReport = calculateRedundantDependencyGraph(this, modules)
                }
            }
        }
    }

    private fun calculateRedundantDependencyGraph(
        dependencies: List<ModuleDependency>,
        modules: Set<String>,
    ): RedundantDependencyGraphReport {
        val redundantGraphs = mutableListOf<RedundantDependency>()

        modules.forEach { module ->
            val moduleDependencies = getModuleDependencies(module, dependencies)
            val duplicates = mutableListOf<ModuleDependency>()
            moduleDependencies.forEach { dep ->
                val filteredDependencies = moduleDependencies.filter { it.dependency == dep.dependency }
                if (filteredDependencies.size > 1) {
                    filteredDependencies.forEach { filteredDep ->
                        if (duplicates.contains(filteredDep).not()) {
                            duplicates.add(filteredDep)
                        }
                    }
                }
            }

            val filteredDuplicate = duplicates.filter { it.configuration == "api" }
            filteredDuplicate.forEach { reason ->
                duplicates.filter { target ->
                    target.dependency == reason.dependency &&
                            target.configuration == "implementation"
                }.forEach { t ->
                    val findResult = redundantGraphs.find { r ->
                        r.target.path == t.path &&
                                r.target.dependency == t.dependency
                    }
                    if (findResult != null) {
                        if (!findResult.reasons.contains(reason)) {
                            redundantGraphs.find { r ->
                                r.target.path == t.path &&
                                        r.target.dependency == t.dependency
                            }?.reasons?.add(reason)
                        }
                    } else {
                        redundantGraphs.add(RedundantDependency(t, mutableListOf(reason)))
                    }
                }
            }
        }

        return RedundantDependencyGraphReport(redundantGraphs)
    }

    private fun getModuleDependencies(
        path: String,
        dependencies: List<ModuleDependency>,
        isApiOnly: Boolean = false
    ): List<ModuleDependency> {
        val directDependencies = when (isApiOnly) {
            true -> dependencies.filter { it.path == path && it.configuration == "api" }
            false -> dependencies.filter { it.path == path }
        }
        val nodeDependencies = mutableListOf<ModuleDependency>()

        directDependencies.forEach { directDep ->
            nodeDependencies.addAll(
                dependencies.filter {
                    it.path == directDep.dependency
                            && it.configuration == "api"
                }
            )
        }

        nodeDependencies
            .filter { it.configuration == "api" }
            .forEach { nodeDep ->
                getModuleDependencies(nodeDep.path, dependencies, isApiOnly = true)
                    .forEach { dep ->
                        if (!nodeDependencies.contains(dep)) {
                            nodeDependencies.add(dep)
                        }
                    }
            }

        return directDependencies + nodeDependencies
    }

}
