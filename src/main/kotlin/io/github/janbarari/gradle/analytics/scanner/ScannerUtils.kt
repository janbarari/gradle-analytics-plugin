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

import io.github.janbarari.gradle.analytics.config.PluginExtension
import io.github.janbarari.gradle.utils.getRequestedTasks
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry

@Suppress("UnstableApiUsage")
fun setupScannerServices(project: Project, registry: BuildEventsListenerRegistry, pluginExtension: PluginExtension) {
    setupInitializationService(project)
    setupDependencyResolutionService(project)
    setupConfigurationService(project)
    setupExecutionService(project, registry, pluginExtension)
}

@Suppress("UnstableApiUsage")
private fun setupExecutionService(
    project: Project,
    registry: BuildEventsListenerRegistry,
    pluginExtension: PluginExtension
) {
    project.gradle.projectsEvaluated {
        val buildExecutionService = project.gradle.sharedServices.registerIfAbsent(
            BuildExecutionService::class.java.simpleName,
            BuildExecutionService::class.java
        ) { spec ->
            with(spec.parameters) {
                databaseConfig.set(pluginExtension.getDatabaseExtension())
                envCI.set(project.providers.environmentVariable("CI").isPresent)
                requestedTasks.set(project.gradle.getRequestedTasks())
                trackingTasks.set(pluginExtension.trackingTasks)
                trackingBranches.set(pluginExtension.trackingBranches)
            }
        }
        registry.onTaskCompletion(buildExecutionService)
    }
}

private fun setupInitializationService(project: Project) {
    project.gradle.addBuildListener(BuildInitializationService(project.gradle))
}

private fun setupConfigurationService(project: Project) {
    project.gradle.addBuildListener(BuildConfigurationService())
}

private fun setupDependencyResolutionService(project: Project) {
    project.gradle.addBuildListener(BuildDependencyResolutionService())
}
