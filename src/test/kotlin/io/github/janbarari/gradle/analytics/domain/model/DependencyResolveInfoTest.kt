package io.github.janbarari.gradle.analytics.domain.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DependencyResolveInfoTest {

    @Test
    fun `check getDuration() returns correct result`() {
        val dri = DependencyResolveInfo(
            "assembleDebug",
            0,
            1600
        )
        assertEquals(1600, dri.getDuration())
    }

    @Test
    fun `check getDuration() returns zero when startedAt and finishedAt not set`() {
        val dri = DependencyResolveInfo(
            "assembleDebug",
            startedAt = 0L
        )
        assertEquals(0, dri.getDuration())
    }

    @Test
    fun `check getDuration() returns zero when finishedAt not set`() {
        val dri = DependencyResolveInfo(
            "assembleDebug",
            startedAt = 100
        )
        assertEquals(0, dri.getDuration())
    }

    @Test
    fun `check getters`() {
        val dri = DependencyResolveInfo(
            "assembleDebug",
            startedAt = 100
        )
        assertEquals(100, dri.startedAt)
        assertEquals(0, dri.finishedAt)
        assertEquals("assembleDebug", dri.path)
    }

    @Test
    fun `check setters`() {
        val dri = DependencyResolveInfo(
            "assembleDebug",
            startedAt = 100
        )
        dri.finishedAt = 10
        assertEquals(10, dri.finishedAt)
    }

}