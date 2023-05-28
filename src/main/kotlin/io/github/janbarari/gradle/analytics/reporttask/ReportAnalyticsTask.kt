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
package io.github.janbarari.gradle.analytics.reporttask

import io.github.janbarari.gradle.ExcludeJacocoGenerated
import io.github.janbarari.gradle.analytics.DatabaseConfig
import io.github.janbarari.gradle.analytics.GradleAnalyticsPlugin
import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import io.github.janbarari.gradle.analytics.reporttask.exception.EmptyMetricsException
import io.github.janbarari.gradle.extension.envCI
import io.github.janbarari.gradle.utils.ConsolePrinter
import io.github.janbarari.gradle.analytics.domain.model.Module
import io.github.janbarari.gradle.analytics.domain.model.Module.Companion.toModule
import io.github.janbarari.gradle.extension.createTask
import io.github.janbarari.gradle.extension.isModuleProject
import io.github.janbarari.gradle.logger.Tower
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.JavaVersion
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.util.GradleVersion
import org.gradle.work.DisableCachingByDefault

/**
 * A Gradle task that generates the report based on `git branch`, `time period` and `task`.
 *
 * A quick instruction about how to invoke the task:
 * `./gradlew reportAnalytics --branch="{your-branch}"
 *                            --task="{your-task}"
 *                            --period can be like "today", "s:yyyy/MM/dd,e:yyyy/MM/dd", "1y", "4m", "38d", "3m 06d"`
 */
@ExcludeJacocoGenerated
@DisableCachingByDefault
abstract class ReportAnalyticsTask : DefaultTask() {

    companion object {
        const val TASK_NAME = "reportAnalytics"
        private val clazz = ReportAnalyticsTask::class.java

        @ExcludeJacocoGenerated
        fun register(config: GradleAnalyticsPluginConfig) {
            val task = config.project.createTask<ReportAnalyticsTask>(TASK_NAME)
            config.project.gradle.projectsEvaluated {
                with(task) {
                    projectNameProperty.set(config.project.rootProject.name)
                    envCIProperty.set(envCI())
                    outputPathProperty.set(config.outputPath)
                    trackingTasksProperty.set(config.trackingTasks)
                    trackingBranchesProperty.set(config.trackingBranches)
                    databaseConfigProperty.set(config.getDatabaseConfig())
                    modules.set(
                        config.project.subprojects
                            .filter { it.isModuleProject() }
                            .map { it.toModule() }
                            .toSet()
                    )
                    excludeModules.set(config.excludeModules)
                    // disable the task from being cached or reuse the outputs on incremental builds.
                    outputs.cacheIf { false }
                    outputs.upToDateWhen { false }
                }
            }
        }
    }

    @set:Option(option = "branch", description = "Git branch name")
    @get:Input
    var branchArgument: String = ""

    @set:Option(option = "task", description = "Tracking task path")
    @get:Input
    var requestedTasksArgument: String = ""

    @set:Option(option = "period", description = "Report period")
    @get:Input
    var periodArgument: String = ""

    @get:Input
    abstract val projectNameProperty: Property<String>

    @get:Input
    abstract val envCIProperty: Property<Boolean>

    @get:Input
    abstract val outputPathProperty: Property<String>

    @get:Input
    abstract val trackingTasksProperty: ListProperty<String>

    @get:Input
    abstract val trackingBranchesProperty: ListProperty<String>

    @get:Input
    abstract val databaseConfigProperty: Property<DatabaseConfig>

    @get:Input
    abstract val modules: SetProperty<Module>

    @get:Input
    abstract val excludeModules: SetProperty<String>

    private lateinit var tower: Tower

    /**
     * Invokes when the task execution process started.
     */
    @TaskAction
    fun execute() = runBlocking {
        val injector = ReportAnalyticsInjector(
            requestedTasks = requestedTasksArgument,
            isCI = envCIProperty.get(),
            databaseConfig = databaseConfigProperty.get(),
            branch = branchArgument,
            outputPath = outputPathProperty.get(),
            projectName = projectNameProperty.get(),
            modules = modules.get().map { it.path }.toSet(),
            excludeModules = excludeModules.get()
        )
        tower = injector.provideTower()

        tower.r("report process started")
        tower.r("plugin version: ${GradleAnalyticsPlugin.PLUGIN_VERSION}")
        tower.r("jvm: ${JavaVersion.current()}")
        tower.r("gradle version: ${GradleVersion.current().version}")
        tower.r("requested tasks: $requestedTasksArgument")
        tower.r("modules count: ${modules.get().size}")
        tower.r("ci: ${envCIProperty.get()}")
        tower.r("tracking tasks count: ${trackingTasksProperty.get().size}")
        tower.r("tracking branches count: ${trackingBranchesProperty.get().size}")

        with(injector.provideReportAnalyticsLogic()) {
            ensureBranchArgumentValid(branchArgument)
            ensurePeriodArgumentValid(periodArgument)
            ensureTaskArgumentValid(requestedTasksArgument)
            try {
                val reportPath = saveReport(generateReport(branchArgument, requestedTasksArgument, periodArgument))
                printSuccessfulResult(reportPath)
            } catch (e: EmptyMetricsException) {
                tower.e(clazz, e.message)
                printNoData()
            }
        }

        tower.r("report process finished")
        tower.save()
    }

    private fun printSuccessfulResult(reportPath: String) {
        tower.i(clazz, "printSuccessfulResult()")
        ConsolePrinter(blockCharWidth = reportPath.length).run {
            printFirstLine()
            printLine(left = "Gradle Analytics Plugin")
            printBreakLine(char = '-')
            printLine(left = "Report generated successfully")
            printLine(left = reportPath)
            printBreakLine(char = '-')
            printLine(left = "Made with ❤ for everyone")
            printLine(left = "https://github.com/janbarari/gradle-analytics-plugin")
            printLine(right = " ↖ Tap the ☆ button to support us")
            printLastLine()
        }
    }

    private fun printNoData() {
        tower.i(clazz, "printNoData()")
        val message = listOf(
            "There is no data to process. Please check the plugin configuration",
            "and wait until the first desired task information is saved."
        )
        ConsolePrinter(blockCharWidth = message[0].length).run {
            printFirstLine()
            printLine(left = "Gradle Analytics Plugin")
            printBreakLine(char = '-')
            printLine(left = message[0])
            printLine(left = message[1])
            printLastLine()
        }
    }
}
