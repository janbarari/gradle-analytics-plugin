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

import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import io.github.janbarari.gradle.analytics.data.DatabaseRepositoryImp
import io.github.janbarari.gradle.analytics.data.database.Database
import io.github.janbarari.gradle.analytics.domain.model.AnalyticsReport
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.metric.configuration.ConfigurationMetricReportStage
import io.github.janbarari.gradle.analytics.metric.initialization.InitializationMetricRenderStage
import io.github.janbarari.gradle.analytics.metric.initialization.InitializationMetricReportStage
import io.github.janbarari.gradle.extension.envCI
import io.github.janbarari.gradle.extension.getSafeResourceAsStream
import io.github.janbarari.gradle.extension.getTextResourceContent
import io.github.janbarari.gradle.extension.hasSpace
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.registerTask
import io.github.janbarari.gradle.extension.toRealPath
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
        generateReport()
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

    private fun getBuildMetrics(): List<BuildMetric> {
        val database = Database(databaseConfig.get(), envCIProperty.get().isPresent)
        val repo: DatabaseRepository = DatabaseRepositoryImp(database, branchArgument, taskArgument)
        return repo.getMetrics(periodArgument.toLong())
    }

    private fun generateReport() {
        val rawHTML: String = getTextResourceContent("index-template.html")
        val data = getBuildMetrics()

        val analyticsReport = AnalyticsReportPipeline(InitializationMetricReportStage(data))
            .addStage(ConfigurationMetricReportStage(data))
            .execute(AnalyticsReport(branch = branchArgument, requestedTasks = taskArgument))

        val renderedHTML = ReportRenderPipeline(InitialRenderStage.Builder()
                .data(data)
                .projectName(projectNameProperty.get())
                .branch(branchArgument)
                .period(periodArgument.toLong())
                .requestedTasks(taskArgument)
                .isCI(envCIProperty.get().isPresent)
                .build())
            .addStage(InitializationMetricRenderStage(analyticsReport))
            .execute(rawHTML)

        saveReport(renderedHTML)

    }

    private fun saveReport(renderedHTML: String) {
        val fontPath = "res/nunito.ttf"
        val chartJsPath = "res/chart.js"
        val logoPath = "res/plugin-logo.png"
        val stylesPath = "res/styles.css"
        val indexPath = "index.html"
        val savePath = "${outputPathProperty.get().toRealPath()}/gradle-analytics-plugin"

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/$fontPath"),
            File("$savePath/$fontPath")
        )

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/$chartJsPath"),
            File("$savePath/$chartJsPath")
        )

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/$logoPath"),
            File("$savePath/$logoPath")
        )

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/$stylesPath"),
            File("$savePath/$stylesPath")
        )

        File("$savePath/$indexPath")
            .writeText(renderedHTML)
    }

}
