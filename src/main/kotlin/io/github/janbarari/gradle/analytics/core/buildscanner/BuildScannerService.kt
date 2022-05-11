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
package io.github.janbarari.gradle.analytics.core.buildscanner

import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginExtension
import io.github.janbarari.gradle.analytics.core.buildscanner.service.BuildConfigurationService
import io.github.janbarari.gradle.analytics.core.buildscanner.service.BuildDependencyResolutionService
import io.github.janbarari.gradle.analytics.core.buildscanner.service.BuildExecutionService
import io.github.janbarari.gradle.analytics.core.buildscanner.service.BuildInitializationService
import org.gradle.api.invocation.Gradle
import org.gradle.build.event.BuildEventsListenerRegistry

/**
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
@Suppress("UnstableApiUsage")
class BuildScannerService(
    private var gradle: Gradle,
    private var registry: BuildEventsListenerRegistry,
    private var pluginExtension: GradleAnalyticsPluginExtension
) {

    init {
        setupExecutionService()
        setupInitializationService()
        setupConfigurationService()
        setupDependencyResolutionService()
    }

    private fun setupExecutionService() {
        gradle.projectsEvaluated {
            val buildExecutionService = gradle.sharedServices.registerIfAbsent(
                BuildExecutionService::class.java.simpleName,
                BuildExecutionService::class.java
            ) { spec ->
                spec.parameters.databaseConfig.set(pluginExtension.getDatabaseConfig())
            }
            registry.onTaskCompletion(buildExecutionService)
        }
    }

    private fun setupInitializationService() {
        gradle.addBuildListener(BuildInitializationService(gradle))
    }

    private fun setupConfigurationService() {
        gradle.addBuildListener(BuildConfigurationService())
    }

    private fun setupDependencyResolutionService() {
        gradle.addBuildListener(BuildDependencyResolutionService())
    }

}
