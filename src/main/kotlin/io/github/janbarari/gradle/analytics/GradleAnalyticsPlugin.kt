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

import io.github.janbarari.gradle.analytics.core.buildscanner.BuildScannerService
import io.github.janbarari.gradle.analytics.data.database.SQLiteDatabase
import io.github.janbarari.gradle.utils.ProjectUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry
import javax.inject.Inject

/**
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
@Suppress("UnstableApiUsage")
class GradleAnalyticsPlugin @Inject constructor(
    private val registry: BuildEventsListenerRegistry
) : Plugin<Project> {

    companion object {
        private const val PLUGIN_DISPLAY_NAME = "GradleAnalyticsPlugin"
        private const val PLUGIN_EXTENSION_NAME = "gradleAnalyticsPlugin"
    }

    override fun apply(project: Project) {
        ensureProjectGradleCompatible()
        setupPluginExtension(project) {
            SQLiteDatabase.connect(it.getDatabaseConfig())
        }
        BuildScannerService(project.gradle, registry)
    }

    /**
     * GradleAnalyticsPlugin is compatible with Gradle version 6.1 and above
     * @throws GradleIncompatibleException when the Gradle version is not compatible
     */
    private fun ensureProjectGradleCompatible() {
        val requiredGradleVersion = ProjectUtils.GradleVersions.V6_1
        if (!ProjectUtils.isCompatibleWith(requiredGradleVersion)) {
            throw GradleIncompatibleException(PLUGIN_DISPLAY_NAME, requiredGradleVersion.versionNumber)
        }
    }

    /**
     * Setups plugin extension.
     */
    private fun setupPluginExtension(
        project: Project,
        onEvaluated: (extension: GradleAnalyticsPluginExtension) -> Unit
    ) {
        val extension = project.extensions.create(
            PLUGIN_EXTENSION_NAME,
            GradleAnalyticsPluginExtension::class.java,
            project
        )
        project.gradle.projectsEvaluated {
            onEvaluated(extension)
        }
    }

}
