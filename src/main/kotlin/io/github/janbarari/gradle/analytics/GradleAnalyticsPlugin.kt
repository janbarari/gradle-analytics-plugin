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
import io.github.janbarari.gradle.utils.ProjectUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry
import javax.inject.Inject

@Suppress("UnstableApiUsage")
class GradleAnalyticsPlugin @Inject constructor(
    private val registry: BuildEventsListenerRegistry
) : Plugin<Project> {

    private lateinit var extension: GradleAnalyticsPluginExtension

    override fun apply(project: Project) {
        ensureProjectGradleCompatible()
        extension = createPluginExtension(project)
        BuildScannerService(project.gradle, registry)
    }

    /**
     * GradleAnalyticsPlugin is compatible with Gradle version 6.1 and above
     * @throws GradleIncompatibleException when the Gradle version is not compatible
     */
    private fun ensureProjectGradleCompatible() {
        val requiredGradleVersion = ProjectUtils.GradleVersions.V6_1
        if (!ProjectUtils.isCompatibleWith(requiredGradleVersion)) {
            throw GradleIncompatibleException("Gradle-Analytics-Plugin", requiredGradleVersion.versionNumber)
        }
    }

    /**
     * Creates plugin extension.
     * @return returns the extension instance.
     */
    private fun createPluginExtension(project: Project): GradleAnalyticsPluginExtension {
        return project
            .extensions
            .create("gradleAnalyticsPlugin", GradleAnalyticsPluginExtension::class.java, project)
    }

}
