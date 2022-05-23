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

import io.github.janbarari.gradle.analytics.plugin.config.PluginExtension
import io.github.janbarari.gradle.utils.ProjectUtils
import io.github.janbarari.gradle.utils.getSafeResourceAsStream
import io.github.janbarari.gradle.utils.isNull
import io.github.janbarari.gradle.utils.openSafeStream
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry
import java.io.File
import java.io.IOException
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
    }

    override fun apply(project: Project) {
        ensureProjectGradleCompatible()
        val pluginExtension = setupPluginExtension(project)
        registerReportTask(project)
        setupBuildScannerServices(project, registry, pluginExtension)
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
    private fun setupPluginExtension(project: Project) : PluginExtension {
        return project.extensions.create(
            PLUGIN_NAME,
            PluginExtension::class.java,
            project
        )
    }

    private fun registerReportTask(project: Project) {
        project.tasks.register("reportAnalytics") {
            it.doLast {
                println(it.hasProperty("name"))
                //report()
            }
        }
    }

    fun report() {

        val outputPath = "/Users/workstation/desktop"
        val taskPath = ":app:assembleDebug"
        val branch = "develop"
        val rootProjectName = "Edifier"
        val timePeriodTitle = "3 Months"
        val timePeriodStart = "21/04/2022"
        val timePeriodEnd = "23/07/2022"
        val reportedAt = "May 23, 2022 13:06 PM UTC"
        val isCI = "No"
        val pluginVersion = "1.0.0"

        val initializationMaxValue = "3000"
        val initializationMedianValues = "[200, 300, 400, 450, 340]"
        val initializationMedianLabels = "[\"A\", \"B\", \"C\", \"D\", \"E\"]"

        val configurationMaxValue = "8000"
        val configurationMedianValues = "[3000, 2000, 2600, 3400, 5000]"
        val configurationMedianLabels = "[\"A\", \"B\", \"C\", \"D\", \"E\"]"

        javaClass.getResource("/index-template.html")!!
            .openSafeStream()
            .bufferedReader()
            .use { it.readText() }
            .replace("%root-project-name%", rootProjectName)
            .replace("%task-path%", taskPath)
            .replace("%branch%", branch)
            .replace("%time-period-title%", timePeriodTitle)
            .replace("%time-period-start%", timePeriodStart)
            .replace("%time-period-end%", timePeriodEnd)
            .replace("%reported-at%", reportedAt)
            .replace("%is-ci%", isCI)
            .replace("%plugin-version%", pluginVersion)
            .replace("%initialization-max-value%", initializationMaxValue)
            .replace("%initialization-median-values%", initializationMedianValues)
            .replace("%initialization-median-labels%", initializationMedianLabels)
            .replace("%configuration-max-value%", configurationMaxValue)
            .replace("%configuration-median-values%", configurationMedianValues)
            .replace("%configuration-median-labels%", configurationMedianLabels)
            .also {

                FileUtils.copyInputStreamToFile(
                    javaClass.getSafeResourceAsStream("/res/nunito.ttf"),
                    File("$outputPath/$reportedAt/res/nunito.ttf")
                )

                FileUtils.copyInputStreamToFile(
                    javaClass.getSafeResourceAsStream("/res/chart.js"),
                    File("$outputPath/$reportedAt/res/chart.js")
                )

                FileUtils.copyInputStreamToFile(
                    javaClass.getSafeResourceAsStream("/res/plugin-logo.png"),
                    File("$outputPath/$reportedAt/res/plugin-logo.png")
                )

                FileUtils.copyInputStreamToFile(
                    javaClass.getSafeResourceAsStream("/res/styles.css"),
                    File("$outputPath/$reportedAt/res/styles.css")
                )

                File("$outputPath/$reportedAt/index.html").writeText(it)

            }

    }

}
