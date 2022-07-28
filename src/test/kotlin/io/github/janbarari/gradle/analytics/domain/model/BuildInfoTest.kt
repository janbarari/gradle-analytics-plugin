package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.analytics.domain.model.os.HardwareInfo
import io.github.janbarari.gradle.analytics.domain.model.os.OsInfo
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BuildInfoTest {

    @Test
    fun `check getters`() {
        val info = BuildInfo(
            createdAt = 0,
            startedAt = 0,
            initializedAt = 50,
            configuredAt = 150,
            dependenciesResolveInfo = listOf(),
            executedTasks = listOf(),
            finishedAt = 800,
            osInfo = OsInfo("macOS"),
            hardwareInfo = HardwareInfo(10000, 16000),
            branch = "master",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(0, info.createdAt)
        assertEquals(0, info.startedAt)
        assertEquals(50, info.initializedAt)
        assertEquals(150, info.configuredAt)
        assertEquals(0, info.dependenciesResolveInfo.size)
        assertEquals(0, info.executedTasks.size)
        assertEquals("macOS", info.osInfo.name)
        assertEquals(10000, info.hardwareInfo.availableMemory)
        assertEquals(800, info.finishedAt)
        assertEquals("master", info.branch)
        assertEquals(0, info.requestedTasks.size)
    }

    @Test
    fun `check getTotalDuration() returns correct result`() {
        val info = BuildInfo(
            createdAt = 0,
            startedAt = 50,
            initializedAt = 150,
            configuredAt = 100,
            dependenciesResolveInfo = listOf(),
            executedTasks = listOf(),
            finishedAt = 800,
            osInfo = OsInfo("macOS"),
            hardwareInfo = HardwareInfo(10000, 16000),
            branch = "master",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(750, info.getTotalDuration().toMillis())
    }

    @Test
    fun `check getTotalDuration() returns zero when finishedAt not set`() {
        val info = BuildInfo(
            createdAt = 0,
            startedAt = 2,
            initializedAt = 50,
            configuredAt = 150,
            dependenciesResolveInfo = listOf(),
            executedTasks = listOf(),
            finishedAt = 800,
            osInfo = OsInfo("macOS"),
            hardwareInfo = HardwareInfo(10000, 16000),
            branch = "master",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(798, info.getTotalDuration().toMillis())
    }

    @Test
    fun `check getInitializationDuration() returns correct result`() {
        val info = BuildInfo(
            createdAt = 0,
            startedAt = 0,
            initializedAt = 50,
            configuredAt = 150,
            dependenciesResolveInfo = listOf(),
            executedTasks = listOf(),
            finishedAt = 800,
            osInfo = OsInfo("macOS"),
            hardwareInfo = HardwareInfo(10000, 16000),
            branch = "master",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(50, info.getInitializationDuration().toMillis())
    }

    @Test
    fun `check getInitializationDuration() returns zero when finishedAt not set`() {
        val info = BuildInfo(
            createdAt = 0,
            startedAt = 2,
            initializedAt = 150,
            configuredAt = 100,
            dependenciesResolveInfo = listOf(),
            executedTasks = listOf(),
            finishedAt = 800,
            osInfo = OsInfo("macOS"),
            hardwareInfo = HardwareInfo(10000, 16000),
            branch = "master",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(148, info.getInitializationDuration().toMillis())
    }

    @Test
    fun `check getConfigurationDuration() returns correct result`() {
        val info = BuildInfo(
            createdAt = 0,
            startedAt = 0,
            initializedAt = 50,
            configuredAt = 150,
            dependenciesResolveInfo = listOf(),
            executedTasks = listOf(),
            finishedAt = 800,
            osInfo = OsInfo("macOS"),
            hardwareInfo = HardwareInfo(10000, 16000),
            branch = "master",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(150, info.getConfigurationDuration().toMillis())
    }

    @Test
    fun `check getConfigurationDuration() returns zero when finishedAt not set`() {
        val info = BuildInfo(
            createdAt = 0,
            startedAt = 2,
            initializedAt = 0,
            configuredAt = 0,
            dependenciesResolveInfo = listOf(),
            executedTasks = listOf(),
            finishedAt = 800,
            osInfo = OsInfo("macOS"),
            hardwareInfo = HardwareInfo(10000, 16000),
            branch = "master",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(0, info.getConfigurationDuration().toMillis())
    }

    @Test
    fun `check getExecutionDuration() returns correct result`() {
        val info = BuildInfo(
            createdAt = 0,
            startedAt = 0,
            initializedAt = 50,
            configuredAt = 150,
            dependenciesResolveInfo = listOf(),
            executedTasks = listOf(),
            finishedAt = 800,
            osInfo = OsInfo("macOS"),
            hardwareInfo = HardwareInfo(10000, 16000),
            branch = "master",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(650, info.getExecutionDuration().toMillis())
    }

    @Test
    fun `check getExecutionDuration() returns zero when finishedAt not set`() {
        val info = BuildInfo(
            createdAt = 0,
            startedAt = 400,
            initializedAt = 0,
            configuredAt = 500,
            dependenciesResolveInfo = listOf(),
            executedTasks = listOf(),
            finishedAt = 800,
            osInfo = OsInfo("macOS"),
            hardwareInfo = HardwareInfo(10000, 16000),
            branch = "master",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(300, info.getExecutionDuration().toMillis())
    }

    @Test
    fun `check getTotalDependenciesResolveDuration() returns correct result`() {
        val info = BuildInfo(
            createdAt = 0,
            startedAt = 0,
            initializedAt = 50,
            configuredAt = 150,
            dependenciesResolveInfo = listOf(),
            executedTasks = listOf(),
            finishedAt = 800,
            osInfo = OsInfo("macOS"),
            hardwareInfo = HardwareInfo(10000, 16000),
            branch = "master",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(0, info.getTotalDependenciesResolveDuration().toMillis())
    }

}