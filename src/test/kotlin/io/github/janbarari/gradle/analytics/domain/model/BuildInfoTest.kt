package io.github.janbarari.gradle.analytics.domain.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BuildInfoTest {

    @Test
    fun `check getters`() {
        val info = BuildInfo(
            0,
            50,
            150,
            listOf(),
            listOf(),
            800,
            OsInfo("macOS"),
            HardwareInfo(10000, 16000)
        )
        assertEquals(0, info.startedAt)
        assertEquals(50, info.initializedAt)
        assertEquals(150, info.configuredAt)
        assertEquals(0, info.dependenciesResolveInfo.size)
        assertEquals(0, info.executedTasks.size)
        assertEquals("macOS", info.osInfo.name)
        assertEquals(10000, info.hardwareInfo.availableMemory)
        assertEquals(800, info.finishedAt)
    }

    @Test
    fun `check getTotalDuration() returns correct result`() {
        val info = BuildInfo(
            0,
            50,
            150,
            listOf(),
            listOf(),
            800,
            OsInfo("macOS"),
            HardwareInfo(10000, 16000)
        )
        assertEquals(800, info.getTotalDuration().toMillis())
    }

    @Test
    fun `check getTotalDuration() returns zero when finishedAt not set`() {
        val info = BuildInfo(
            2,
            50,
            150,
            listOf(),
            listOf(),
            0,
            OsInfo("macOS"),
            HardwareInfo(10000, 16000)
        )
        assertEquals(0, info.getTotalDuration().toMillis())
    }

    @Test
    fun `check getInitializationDuration() returns correct result`() {
        val info = BuildInfo(
            0,
            50,
            150,
            listOf(),
            listOf(),
            800,
            OsInfo("macOS"),
            HardwareInfo(10000, 16000)
        )
        assertEquals(50, info.getInitializationDuration().toMillis())
    }

    @Test
    fun `check getInitializationDuration() returns zero when finishedAt not set`() {
        val info = BuildInfo(
            2,
            0,
            150,
            listOf(),
            listOf(),
            0,
            OsInfo("macOS"),
            HardwareInfo(10000, 16000)
        )
        assertEquals(0, info.getInitializationDuration().toMillis())
    }

    @Test
    fun `check getConfigurationDuration() returns correct result`() {
        val info = BuildInfo(
            0,
            50,
            150,
            listOf(),
            listOf(),
            800,
            OsInfo("macOS"),
            HardwareInfo(10000, 16000)
        )
        assertEquals(150, info.getConfigurationDuration().toMillis())
    }

    @Test
    fun `check getConfigurationDuration() returns zero when finishedAt not set`() {
        val info = BuildInfo(
            2,
            0,
            0,
            listOf(),
            listOf(),
            0,
            OsInfo("macOS"),
            HardwareInfo(10000, 16000)
        )
        assertEquals(0, info.getConfigurationDuration().toMillis())
    }

    @Test
    fun `check getExecutionDuration() returns correct result`() {
        val info = BuildInfo(
            0,
            50,
            150,
            listOf(),
            listOf(),
            800,
            OsInfo("macOS"),
            HardwareInfo(10000, 16000)
        )
        assertEquals(650, info.getExecutionDuration().toMillis())
    }

    @Test
    fun `check getExecutionDuration() returns zero when finishedAt not set`() {
        val info = BuildInfo(
            400,
            0,
            500,
            listOf(),
            listOf(),
            0,
            OsInfo("macOS"),
            HardwareInfo(10000, 16000)
        )
        assertEquals(0, info.getExecutionDuration().toMillis())
    }

    @Test
    fun `check getTotalDependenciesResolveDuration() returns correct result`() {
        val info = BuildInfo(
            0,
            50,
            150,
            listOf(
                DependencyResolveInfo("a", 1, 10)
            ),
            listOf(),
            800,
            OsInfo("macOS"),
            HardwareInfo(10000, 16000)
        )
        assertEquals(9, info.getTotalDependenciesResolveDuration().toMillis())
    }

}