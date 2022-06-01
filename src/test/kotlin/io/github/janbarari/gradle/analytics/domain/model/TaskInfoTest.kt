package io.github.janbarari.gradle.analytics.domain.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TaskInfoTest {

    @Test
    fun `check getDuration() returns correct result`() {
        val task = TaskInfo(
            1,
            50,
            "assembleDebug",
            "assemble",
            "assemble"
        )
        assertEquals(49, task.getDuration())
    }

    @Test
    fun `check getters`() {
        val task = TaskInfo(
            1,
            50,
            "assembleDebug",
            "assemble",
            "assemble"
        )
        assertEquals(1, task.startedAt)
        assertEquals(50, task.finishedAt)
        assertEquals("assembleDebug", task.path)
        assertEquals("assemble", task.displayName)
        assertEquals("assemble", task.name)
    }

    @Test
    fun `check getDuration() returns zero when finishedAt is smaller than startedAt`() {
        val task = TaskInfo(
            100,
            50,
            "assembleDebug",
            "assemble",
            "assemble"
        )
        assertEquals(0, task.getDuration())
    }

    @Test
    fun `check getModule() returns 'no_module'`() {
        val task = TaskInfo(
            100,
            50,
            "assembleDebug",
            "assemble",
            "assemble"
        )
        assertEquals("no_module", task.getModule())
    }

    @Test
    fun `check getModule() returns module`() {
        val task = TaskInfo(
            100,
            50,
            ":feature:app:assembleDebug",
            "assemble",
            "assemble"
        )
        assertEquals(":feature:app", task.getModule())
    }

}