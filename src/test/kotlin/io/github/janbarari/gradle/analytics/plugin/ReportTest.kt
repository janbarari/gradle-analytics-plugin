package io.github.janbarari.gradle.analytics.plugin

import io.github.janbarari.gradle.utils.getSafeResourceAsStream
import io.github.janbarari.gradle.utils.isNull
import io.github.janbarari.gradle.utils.openSafeStream
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.Test
import java.io.File
import java.io.IOException

class ReportTest {

    @Test
    fun report() {

        val outputPath = "/Users/workstation/desktop"
        val taskPath = ":app:assembleDebug"
        val branch = "develop"
        val rootProjectName = "Edifier"
        val timePeriodTitle = "3 Months"
        val timePeriodStart = "21/04/2022"
        val timePeriodEnd = "23/07/2022"
        val reportedAt = "May 23, 2022 13:06 PM UST"
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