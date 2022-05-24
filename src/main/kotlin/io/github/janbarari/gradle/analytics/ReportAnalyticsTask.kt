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

import io.github.janbarari.gradle.logger.Logger
import io.github.janbarari.gradle.utils.getSafeResourceAsStream
import io.github.janbarari.gradle.utils.isNull
import io.github.janbarari.gradle.utils.openSafeStream
import org.apache.commons.io.FileUtils
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

abstract class ReportAnalyticsTask: DefaultTask() {

    companion object {
        private const val TASK_NAME = "reportAnalytics"

        private const val BRANCH_PROP = "branch"
        private const val PERIOD_PROP = "period"
        private const val TASK_PROP = "task"

        fun register(project: Project) {
            project.tasks.register(TASK_NAME, ReportAnalyticsTask::class.java)
        }
    }

    @set:Option(option = "branch", description = "Branch deee")
    @get:Input
    var branch: String = ""

    @set:Option(option = "task", description = "Task deee")
    @get:Input
    var task: String = ""

    @set:Option(option = "period", description = "Period deee")
    @get:Input
    var period: String = ""

    @TaskAction
    fun execute() {
        Logger.log("ReportAnalyticsTask", branch)
        Logger.log("ReportAnalyticsTask", period.toString())
        Logger.log("ReportAnalyticsTask", task)
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

    /**
     * Ensures the task required properties are presented.
     *
     * @throws MissingPropertyException if a property is not presented.
     */
    @kotlin.jvm.Throws(MissingPropertyException::class)
    fun ensurePropertiesPresented() {
        if (hasProperty(BRANCH_PROP).not() ||
            hasProperty(PERIOD_PROP).not() ||
            hasProperty(TASK_PROP).not() ) {
            throw MissingPropertyException(
                "$TASK_NAME task properties are missing. " +
                        "Please ensure `branch`, `period` and `task` property is presented!"
            )
        }
    }

    /**
     *
     * @throws InvalidPropertyException if a property invalid.
     */
    @kotlin.jvm.Throws(InvalidPropertyException::class)
    fun ensurePropertiesValid() {
        ensureBranchPropertyValid()
        ensurePeriodPropertyValid()
        ensureTaskPropertyValid()
    }

    fun ensureBranchPropertyValid() {
        val branchProp = property(BRANCH_PROP) as String
        if (branchProp.contains(" "))
            throw InvalidPropertyException("`branch` property is not valid!")
    }

    fun ensurePeriodPropertyValid() {
        val periodProp = property(PERIOD_PROP) as String
        if (periodProp.toIntOrNull().isNull())
            throw InvalidPropertyException("`period` property is not valid!, Period should be a number between 1 to 12")
    }

    fun ensureTaskPropertyValid() {
        val taskProp = property(TASK_PROP) as String
        if (taskProp.startsWith(":").not() || taskProp.contains(" "))
            throw InvalidPropertyException("`task` property is not valid!")
    }

}
