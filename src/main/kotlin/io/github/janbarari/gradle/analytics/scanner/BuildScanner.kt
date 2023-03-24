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
package io.github.janbarari.gradle.analytics.scanner

import io.github.janbarari.gradle.ExcludeJacocoGenerated
import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import io.github.janbarari.gradle.analytics.domain.model.Module.Companion.toModule
import io.github.janbarari.gradle.analytics.scanner.configuration.BuildConfigurationService
import io.github.janbarari.gradle.analytics.scanner.dependencyresolution.BuildDependencyResolutionService
import io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionService
import io.github.janbarari.gradle.analytics.scanner.initialization.BuildInitializationService
import io.github.janbarari.gradle.extension.envCI
import io.github.janbarari.gradle.extension.getNonCacheableTasks
import io.github.janbarari.gradle.extension.getRequestedTasks
import io.github.janbarari.gradle.extension.isModuleProject
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.utils.DependencyGraphGenerator
import org.gradle.api.Project
import org.gradle.api.internal.GradleInternal
import org.gradle.build.event.BuildEventsListenerRegistry
import oshi.SystemInfo
import java.util.*

@ExcludeJacocoGenerated
object BuildScanner {

    fun setup(
        config: GradleAnalyticsPluginConfig,
        registry: BuildEventsListenerRegistry
    ) {
        setupInitializationService(config.project)
        setupDependencyResolutionService(config.project)
        setupConfigurationService(config.project)
        setupExecutionService(config.project, registry, config)
    }

    private fun setupExecutionService(
        project: Project,
        registry: BuildEventsListenerRegistry,
        configuration: GradleAnalyticsPluginConfig
    ) {
        val systemProcessor = SystemInfo().hardware.processor
        val availableWorkerCount = project.gradle.startParameter.maxWorkerCount
        val maximumWorkerCount = systemProcessor.logicalProcessorCount + systemProcessor.physicalProcessorCount

        project.gradle.projectsEvaluated {
            val nonCacheableTasks = Collections.synchronizedList(mutableListOf<String>())
            project.allprojects.whenEach {
                tasks.toList().getNonCacheableTasks().whenNotNull {
                    nonCacheableTasks.addAll(this)
                }
            }

            val subprojects = project.subprojects
                .filter { it.isModuleProject() && !configuration.excludeModules.contains(it.path) }

            val modulesDependencyGraph = DependencyGraphGenerator.generate(subprojects)

            val modules = subprojects.map { it.toModule() }

            val buildExecutionService = project.gradle.sharedServices.registerIfAbsent(
                BuildExecutionService::class.java.simpleName,
                BuildExecutionService::class.java
            ) { spec ->
                with(spec.parameters) {
                    enabled.set(configuration.isEnabled)
                    databaseConfig.set(configuration.getDatabaseConfig())
                    envCI.set(envCI())
                    requestedTasks.set(project.gradle.getRequestedTasks())
                    trackingTasks.set(configuration.trackingTasks)
                    trackingBranches.set(configuration.trackingBranches)
                    trackAllBranchesEnabled.set(configuration.isTrackAllBranchesEnabled)
                    outputPath.set(configuration.outputPath)
                    this.maximumWorkerCount.set(maximumWorkerCount)
                    this.availableWorkerCount.set(availableWorkerCount)
                    this.modules.set(modules)
                    this.modulesDependencyGraph.set(modulesDependencyGraph)
                    this.nonCacheableTasks.set(nonCacheableTasks)
                }
            }
            registry.onTaskCompletion(buildExecutionService)
        }
    }

    private fun setupInitializationService(project: Project) {
        project.gradle.addBuildListener(BuildInitializationService(project))

        // Gradle build listener's `projectsLoaded` function is not invoked during the build process,
        // This is an alternative solution to figure out when the initialization process has been finished.
        (project.gradle as GradleInternal).rootProject {
            BuildInitializationService.assignInitializedAt()
        }
    }

    private fun setupConfigurationService(project: Project) {
        project.gradle.addBuildListener(BuildConfigurationService())
    }

    private fun setupDependencyResolutionService(project: Project) {
        project.gradle.addBuildListener(BuildDependencyResolutionService())
    }

}
