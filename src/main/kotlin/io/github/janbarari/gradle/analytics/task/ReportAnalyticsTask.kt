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
package io.github.janbarari.gradle.analytics.task

import io.github.janbarari.gradle.analytics.GradleAnalyticsPlugin.Companion.PLUGIN_VERSION
import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import io.github.janbarari.gradle.utils.hasSpace
import io.github.janbarari.gradle.utils.isNull
import io.github.janbarari.gradle.utils.openSafeStream
import io.github.janbarari.gradle.utils.registerTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class ReportAnalyticsTask : DefaultTask() {

    companion object {
        private const val TASK_NAME = "reportAnalytics"

        /**
         * it.envCI.set(false)
        it.projectName.set("Mehdi")
        //val pluginExtension = project.getExtension<PluginExtension>()
        it.outputPath.set("/Users/workstation/desktop")
        it.trackingTasks.set(listOf(":app:assembleDebug", "clean", ":app:assembleRelease"))
        it.trackingBranches.set(listOf("master", "develop"))
         */
        fun register(project: Project, configuration: GradleAnalyticsPluginConfig) {
            project.registerTask<ReportAnalyticsTask>(TASK_NAME) {
                test.set(configuration.outputPath)
            }
        }
    }

    @set:Option(option = "branch", description = "Git branch name")
    @get:Input
    var branch: String = ""

    @set:Option(option = "task", description = "Tracking task name")
    @get:Input
    var task: String = ""

    @set:Option(option = "period", description = "Number of months")
    @get:Input
    var period: String = ""

    @get:Input
    abstract val test: Property<String>

    @TaskAction
    fun execute() {
        ensureBranchValid()
        ensurePeriodValid()
        ensureTaskValid()
        //report()
        println("outputPath: ${test.get()}")
    }

    private fun ensureBranchValid() {
        if (branch.isEmpty()) throw MissingPropertyException("`--branch` is not present!")
        if (branch.hasSpace()) throw InvalidPropertyException("`--branch` is not valid!")
    }

    private fun ensurePeriodValid() {
        if (period.isEmpty()) throw MissingPropertyException("`--period` is not present!")
        if (period.toIntOrNull()
                .isNull()
        ) throw InvalidPropertyException("`--period` is not valid!, Period should be a number between 1 to 12.")
    }

    private fun ensureTaskValid() {
        if (task.isEmpty()) throw MissingPropertyException("`--task` is not present!")
        if (task.startsWith(":").not()) throw InvalidPropertyException("`--task` is not valid!")
    }

    fun report() {

        val rootProjectName = "fsfsfs"

        val timePeriodTitle = "$period Months"
        val timePeriodStart = "21/04/2022"
        val timePeriodEnd = "23/07/2022"
        val reportedAt = "May 23, 2022 13:06 PM UTC"
        val isCI = "Yes"
        val pluginVersion = PLUGIN_VERSION

        val initializationMaxValue = "3000"
        val initializationMedianValues = "[200, 300, 400, 450, 340]"
        val initializationMedianLabels = "[\"A\", \"B\", \"C\", \"D\", \"E\"]"

        val configurationMaxValue = "8000"
        val configurationMedianValues = "[3000, 2000, 2600, 3400, 5000]"
        val configurationMedianLabels = "[\"A\", \"B\", \"C\", \"D\", \"E\"]"

        javaClass.getResource("/index-template.html")!!.openSafeStream().bufferedReader().use { it.readText() }
            .replace("%root-project-name%", rootProjectName).replace("%task-path%", task).replace("%branch%", branch)
            .replace("%time-period-title%", timePeriodTitle).replace("%time-period-start%", timePeriodStart)
            .replace("%time-period-end%", timePeriodEnd).replace("%reported-at%", reportedAt).replace("%is-ci%", isCI)
            .replace("%plugin-version%", pluginVersion).replace("%initialization-max-value%", initializationMaxValue)
            .replace("%initialization-median-values%", initializationMedianValues)
            .replace("%initialization-median-labels%", initializationMedianLabels)
            .replace("%configuration-max-value%", configurationMaxValue)
            .replace("%configuration-median-values%", configurationMedianValues)
            .replace("%configuration-median-labels%", configurationMedianLabels).also {

//                FileUtils.copyInputStreamToFile(
//                    javaClass.getSafeResourceAsStream("/res/nunito.ttf"),
            //                    File("$outputPath/$reportedAt/res/nunito.ttf")
//                )
//
//                FileUtils.copyInputStreamToFile(
//                    javaClass.getSafeResourceAsStream("/res/chart.js"),
            //                    File("$outputPath/$reportedAt/res/chart.js")
//                )
//
//                FileUtils.copyInputStreamToFile(
//                    javaClass.getSafeResourceAsStream("/res/plugin-logo.png"),
//                    File("$outputPath/$reportedAt/res/plugin-logo.png")
//                )
//
//                FileUtils.copyInputStreamToFile(
//                    javaClass.getSafeResourceAsStream("/res/styles.css"),
            //                    File("$outputPath/$reportedAt/res/styles.css")
//                )
//
//                File("$outputPath/$reportedAt/index.html").writeText(it)

            }

    }

}
