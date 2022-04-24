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
package io.github.janbarari.gradle.analytics.plugin

import io.github.janbarari.gradle.analytics.core.exception.GradleNotCompatibleException
import io.github.janbarari.gradle.analytics.core.gradlebuild.BuildReport
import io.github.janbarari.gradle.analytics.core.gradlebuild.GradleBuild
import io.github.janbarari.gradle.analytics.core.task.TasksLifecycleParams
import io.github.janbarari.gradle.analytics.core.task.TasksLifecycleService
import io.github.janbarari.gradle.bus.Observer
import io.github.janbarari.gradle.utils.GradleVersionUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry
import javax.inject.Inject

@Suppress("UnstableApiUsage")
class GradleAnalyticsPlugin @Inject constructor(
    private val registry: BuildEventsListenerRegistry
) : Plugin<Project>, GradleBuild.OnBuildListener {

    override fun apply(project: Project) {
        ensureGradleVersionIsCompatible()
        val receiverGUID = Observer.generateGUID()
        val tasksLifecycleServiceClazz = TasksLifecycleService::class.java
        val tasksLifecycleService = project.gradle.sharedServices.registerIfAbsent(
            tasksLifecycleServiceClazz.simpleName,
            tasksLifecycleServiceClazz
        ) { spec ->
            val params = TasksLifecycleParams(receiverGUID)
            spec.parameters.getParams().set(params)
        }
        registry.onTaskCompletion(tasksLifecycleService)
    }

    /**
     * GradleAnalyticsPlugin is compatible with Gradle version 6.1 and above
     * @throws GradleNotCompatibleException when the Gradle version is not compatible
     */
    private fun ensureGradleVersionIsCompatible() {
        if(!GradleVersionUtils.isCompatibleWith(GradleVersionUtils.GradleVersions.V6_1)) {
            throw GradleNotCompatibleException(
                "Gradle-Analytics-Plugin",
                GradleVersionUtils.GradleVersions.V6_1.versionNumber
            )
        }
    }

    override fun onBuildStarted() {
        //todo add logic
    }

    override fun onBuildFinished(buildReport: BuildReport) {
        //todo add logic
    }

}
