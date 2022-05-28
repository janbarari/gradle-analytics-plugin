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

import io.github.janbarari.gradle.analytics.GradleAnalyticsPlugin.Companion.PLUGIN_VERSION
import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import io.github.janbarari.gradle.analytics.configurationmetric.ConfigurationMetricReportStage
import io.github.janbarari.gradle.analytics.data.DatabaseRepositoryImp
import io.github.janbarari.gradle.analytics.data.database.Database
import io.github.janbarari.gradle.analytics.domain.model.AnalyticsReport
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.initializationmetric.InitializationMetricRenderStage
import io.github.janbarari.gradle.analytics.initializationmetric.InitializationMetricReportStage
import io.github.janbarari.gradle.extension.envCI
import io.github.janbarari.gradle.extension.getSafeResourceAsStream
import io.github.janbarari.gradle.extension.hasSpace
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.openSafeStream
import io.github.janbarari.gradle.extension.registerTask
import io.github.janbarari.gradle.extension.toRealPath
import io.github.janbarari.gradle.utils.DateTimeUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

/**
 * A Gradle task that generates the report based on `git branch`, `time period` and `task name`.
 *
 * A quick instruction about how to invoke the task:
 * `./gradlew reportAnalytics --branch="{your-branch}" --task="{your-task}" --period="{a-number-between-1-to-12}"`
 */
abstract class ReportAnalyticsTask : DefaultTask() {

    companion object {
        private const val TASK_NAME = "reportAnalytics"

        fun register(project: Project, configuration: GradleAnalyticsPluginConfig) {
            project.registerTask<ReportAnalyticsTask>(TASK_NAME) {
                projectNameProperty.set(project.rootProject.name)
                envCIProperty.set(project.envCI())
                outputPathProperty.set(configuration.outputPath)
                trackingTasksProperty.set(configuration.trackingTasks)
                trackingBranchesProperty.set(configuration.trackingBranches)
                databaseConfig.set(configuration.getDatabaseConfig())
            }
        }
    }

    @set:Option(option = "branch", description = "Git branch name")
    @get:Input
    var branchArgument: String = ""

    @set:Option(option = "task", description = "Tracking task name")
    @get:Input
    var taskArgument: String = ""

    @set:Option(option = "period", description = "Number of months")
    @get:Input
    var periodArgument: String = ""

    @get:Input
    abstract val projectNameProperty: Property<String>

    @get:Input
    abstract val envCIProperty: Property<Provider<String>>

    @get:Input
    abstract val outputPathProperty: Property<String>

    @get:Input
    abstract val trackingTasksProperty: ListProperty<String>

    @get:Input
    abstract val trackingBranchesProperty: ListProperty<String>

    @get:Input
    abstract val databaseConfig: Property<GradleAnalyticsPluginConfig.DatabaseConfig>

    /**
     * Invokes when the task execution process started.
     */
    @TaskAction
    fun execute() {
        ensureBranchArgumentValid()
        ensurePeriodArgumentValid()
        ensureTaskArgumentValid()
        report()
    }

    /**
     * Ensures the `--branch` input argument is set and valid.
     */
    @kotlin.jvm.Throws(MissingPropertyException::class, InvalidPropertyException::class)
    private fun ensureBranchArgumentValid() {
        if (branchArgument.isEmpty()) throw MissingPropertyException("`--branch` is not present!")
        if (branchArgument.hasSpace()) throw InvalidPropertyException("`--branch` is not valid!")
    }

    /**
     * Ensures the `--period` input argument is set and valid.
     */
    @kotlin.jvm.Throws(MissingPropertyException::class, InvalidPropertyException::class)
    private fun ensurePeriodArgumentValid() {
        if (periodArgument.isEmpty()) throw MissingPropertyException("`--period` is not present!")
        if (periodArgument.toIntOrNull()
                .isNull()
        ) throw InvalidPropertyException("`--period` is not valid!, Period should be a number between 1 to 12.")
    }

    /**
     * Ensures the `--task` input argument is set and valid.
     */
    @kotlin.jvm.Throws(MissingPropertyException::class, InvalidPropertyException::class)
    private fun ensureTaskArgumentValid() {
        if (taskArgument.isEmpty()) throw MissingPropertyException("`--task` is not present!")
        if (taskArgument.startsWith(":").not()) throw InvalidPropertyException("`--task` is not valid!")
    }

    private fun getData(): List<BuildMetric> {
        val database = Database(databaseConfig.get(), envCIProperty.get().isPresent)
        val repo: DatabaseRepository = DatabaseRepositoryImp(database, branchArgument, taskArgument)
        return repo.getMetrics(periodArgument.toLong())
    }

    @Suppress("LongMethod")
    fun report() {

        val data = getData()

        val analyticsReport =
            AnalyticsReportPipeline(InitializationMetricReportStage(data)).addStage(ConfigurationMetricReportStage(data))
                .execute(
                    AnalyticsReport(
                        branch = branchArgument, requestedTasks = taskArgument
                    )
                )

        println(analyticsReport.toJson())

        val rawHTML: String = javaClass.getResource("/index-template.html")!!.openSafeStream().bufferedReader().use { it.readText() }
                .replace("%root-project-name%", projectNameProperty.get()).replace("%task-path%", taskArgument)
                .replace("%branch%", branchArgument).replace("%time-period-title%", "$periodArgument Months")
                .replace("%time-period-start%", DateTimeUtils.msToDateString(System.currentTimeMillis()))
                .replace("%time-period-end%", DateTimeUtils.msToDateString(System.currentTimeMillis()))
                .replace("%reported-at%", DateTimeUtils.msToDateTimeString(System.currentTimeMillis()))
                .replace("%is-ci%", if (envCIProperty.get().isPresent) "Yes" else "No")
                .replace("%plugin-version%", PLUGIN_VERSION)

        val renderedHTML = ReportRenderPipeline(InitializationMetricRenderStage(analyticsReport)).execute(rawHTML)

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/res/nunito.ttf"),
            File("${outputPathProperty.get().toRealPath()}/res/nunito.ttf")
        )

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/res/chart.js"),
            File("${outputPathProperty.get().toRealPath()}/res/chart.js")
        )

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/res/plugin-logo.png"),
            File("${outputPathProperty.get().toRealPath()}/res/plugin-logo.png")
        )

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/res/styles.css"),
            File("${outputPathProperty.get().toRealPath()}/res/styles.css")
        )

        File("${outputPathProperty.get().toRealPath()}/index.html").writeText(renderedHTML)

    }

}
