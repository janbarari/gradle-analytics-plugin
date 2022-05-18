package io.github.janbarari.gradle.analytics.core.buildscanner.model

import io.github.janbarari.gradle.analytics.plugin.buildscanner.model.TaskInfo
import org.junit.jupiter.api.Test

class TaskInfoTest {

    @Test
    fun `check getModule() function returns the module name`() {
        val task = TaskInfo(
            0L,
            1L,
            ":feature:list:assembleDebug",
            "assembleDebug",
            "assembleDebug"
        )
        println(task.getModule())
    }

}