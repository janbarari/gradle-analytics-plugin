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

import io.github.janbarari.gradle.ExcludeJacocoGenerated
import io.github.janbarari.gradle.analytics.database.MySqlDatabaseConnection
import io.github.janbarari.gradle.analytics.database.SqliteDatabaseConnection
import io.github.janbarari.gradle.analytics.reporttask.ReportAnalyticsTask
import io.github.janbarari.gradle.analytics.scanner.ScannerUtils
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.extension.whenTypeIs
import io.github.janbarari.gradle.utils.GitUtils
import io.github.janbarari.gradle.utils.ProjectUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry
import javax.inject.Inject

/**
 * A free Gradle plugin for analytics of your projects. Provides unique visual and
 * text metrics in HTML format.
 */
@ExcludeJacocoGenerated
class GradleAnalyticsPlugin @Inject constructor(
    private val registry: BuildEventsListenerRegistry
) : Plugin<Project> {

    companion object {
        const val PLUGIN_NAME = "gradleAnalyticsPlugin"
        const val PLUGIN_VERSION = "1.0.0-beta2"
    }

    /**
     * Gradle will invoke this function once the plugin is added into the project build script.
     */
    override fun apply(project: Project) {
        ensureProjectGradleCompatible()
        ensureGitTerminalAccessible()
        val config = setupPluginConfig(project)
        ensureConfigValid(config)
        registerTasks(config)
        ScannerUtils.setupScannerServices(config, registry)
    }

    /**
     * The plugin is compatible with Gradle version 6.1 and above, This function ensures
     * the plugin Gradle version is compatible with the user project version.
     *
     * @throws io.github.janbarari.gradle.analytics.IncompatibleVersionException when the Gradle version is not compatible.
     */
    @kotlin.jvm.Throws(IncompatibleVersionException::class)
    private fun ensureProjectGradleCompatible() {
        val requiredGradleVersion = ProjectUtils.GradleVersions.V6_1
        if (!ProjectUtils.isCompatibleWith(requiredGradleVersion)) {
            throw IncompatibleVersionException(requiredGradleVersion.versionNumber)
        }
    }

    /**
     * The plugin only works on projects which use Git. This function ensures the Git terminal accessible in project directory.
     */
    @kotlin.jvm.Throws(NotAccessibleGitTerminalException::class)
    private fun ensureGitTerminalAccessible() {
        try {
            GitUtils.currentBranch()
        } catch (e: Throwable) {
            throw NotAccessibleGitTerminalException()
        }
    }

    /**
     * Setups plugin config.
     *
     * Note: extension will be initialized after projectsEvaluated(configuration process).
     */
    private fun setupPluginConfig(project: Project): GradleAnalyticsPluginConfig {
        return project.extensions.create(
            PLUGIN_NAME,
            GradleAnalyticsPluginConfig::class.java,
            project
        )
    }

    /**
     * Registers the plugin custom tasks.
     */
    private fun registerTasks(config: GradleAnalyticsPluginConfig) {
        ReportAnalyticsTask.register(config)
    }

    /**
     * Ensure the plugin config inputs are valid.
     * @throws PluginConfigNotValidException when something is missing or wrong.
     */
    @kotlin.jvm.Throws(PluginConfigNotValidException::class)
    @Suppress("ThrowsCount")
    private fun ensureConfigValid(config: GradleAnalyticsPluginConfig) {
        config.project.gradle.projectsEvaluated {
            config.getDatabaseConfig().local.whenNotNull {
                whenTypeIs<SqliteDatabaseConnection> {
                    if (path.isNull()) {
                        throw PluginConfigNotValidException(
                            "`path` is missing in local Sqlite database configuration.",
                            config.project.buildFile
                        )
                    }
                    if (name.isNull()) {
                        throw PluginConfigNotValidException(
                            "`name` is missing in local Sqlite database configuration.",
                            config.project.buildFile
                        )
                    }
                }
                whenTypeIs<MySqlDatabaseConnection> {
                    if (host.isNull()) {
                        throw PluginConfigNotValidException(
                            "`host` is missing in local MySql database configuration.",
                            config.project.buildFile
                        )
                    }
                    if (name.isNull()) {
                        throw PluginConfigNotValidException(
                            "`name` is missing in local MySql database configuration.",
                            config.project.buildFile
                        )
                    }
                }
            }
            config.getDatabaseConfig().ci.whenNotNull {
                whenTypeIs<SqliteDatabaseConnection> {
                    if (path.isNull()) {
                        throw PluginConfigNotValidException(
                            "`path` is missing in ci Sqlite database configuration.",
                            config.project.buildFile
                        )
                    }
                    if (name.isNull()) {
                        throw PluginConfigNotValidException(
                            "`name` is missing in ci Sqlite database configuration.",
                            config.project.buildFile
                        )
                    }
                }
                whenTypeIs<MySqlDatabaseConnection> {
                    if (host.isNull()) {
                        throw PluginConfigNotValidException(
                            "`host` is missing in ci MySql database configuration.",
                            config.project.buildFile
                        )
                    }
                    if (name.isNull()) {
                        throw PluginConfigNotValidException(
                            "`name` is missing in ci MySql database configuration.",
                            config.project.buildFile
                        )
                    }
                }
            }
        }
    }

}
