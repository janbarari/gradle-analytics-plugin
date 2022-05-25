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
package io.github.janbarari.gradle.analytics

import io.github.janbarari.gradle.analytics.scanner.setupScannerServices
import io.github.janbarari.gradle.analytics.task.ReportAnalyticsTask
import io.github.janbarari.gradle.utils.ProjectUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry
import javax.inject.Inject

/**
 * A free Gradle plugin for analytics of your projects. Provides unique visual and
 * text metrics in HTML format.
 *
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
@Suppress("UnstableApiUsage")
class GradleAnalyticsPlugin @Inject constructor(
    private val registry: BuildEventsListenerRegistry
) : Plugin<Project> {

    companion object {
        const val PLUGIN_NAME = "gradleAnalyticsPlugin"
        const val PLUGIN_VERSION = "1.0.0"
    }

    override fun apply(project: Project) {
        ensureProjectGradleCompatible()
        val configuration = setupPluginConfiguration(project)
        registerTasks(project, configuration)
        setupScannerServices(project, registry, configuration)
    }

    /**
     * The plugin is compatible with Gradle version 6.1 and above, This function ensures
     * the plugin Gradle version is compatible with the user project version.
     *
     * @throws IncompatibleVersionException when the Gradle version is not compatible.
     */
    @kotlin.jvm.Throws(IncompatibleVersionException::class)
    private fun ensureProjectGradleCompatible() {
        val requiredGradleVersion = ProjectUtils.GradleVersions.V6_1
        if (!ProjectUtils.isCompatibleWith(requiredGradleVersion)) {
            throw IncompatibleVersionException(requiredGradleVersion.versionNumber)
        }
    }

    /**
     * Setups plugin extension.
     *
     * Note: extension will be initialized after projectsEvaluated(configuration process).
     */
    private fun setupPluginConfiguration(project: Project) : GradleAnalyticsPluginConfig {
        return project.extensions.create(
            PLUGIN_NAME,
            GradleAnalyticsPluginConfig::class.java,
            project
        )
    }

    /**
     * Registers the plugin custom tasks.
     */
    private fun registerTasks(project: Project, configuration: GradleAnalyticsPluginConfig) {
        ReportAnalyticsTask.register(project, configuration)
    }

}
