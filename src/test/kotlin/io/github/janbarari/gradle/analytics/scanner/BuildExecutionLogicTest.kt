package io.github.janbarari.gradle.analytics.scanner

import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionInjector
import io.github.janbarari.gradle.analytics.scanner.execution.provideBuildExecutionLogic
import io.github.janbarari.gradle.utils.GitUtils
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BuildExecutionLogicTest {

    private var injector = BuildExecutionInjector(
        trackingBranches = listOf("master"),
        databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
            local = sqlite {
                path = "./build"
            }
        },
        isCI = false,
        branch = "master",
        requestedTasks = listOf("assembleDebug"),
        trackingTasks = listOf("assembleDebug"),
        modulesInfo = emptyList()
    )

    @Test
    fun `check isBranchTrackable() returns correct result`() {
        mockkObject(GitUtils)
        every { GitUtils.currentBranch() } returns "master"

        assertEquals(true, injector.provideBuildExecutionLogic().isBranchTrackable())
    }

    @Test
    fun `check isTaskTrackable() returns correct result`() {
        assertEquals(true, injector.provideBuildExecutionLogic().isTaskTrackable())
    }

    @Test
    fun `check isForbiddenTasksRequested() returns correct result`() {
        injector.requestedTasks = listOf("reportAnalytics")
        assertEquals(true, injector.provideBuildExecutionLogic().isForbiddenTasksRequested())
    }

    @Test
    fun `check isDatabaseConfigurationValid() returns true when ran on Local`() {
        injector.databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
            local = sqlite {
                path = "./build"
            }
        }
        assertEquals(true, injector.provideBuildExecutionLogic().isDatabaseConfigurationValid())
    }

    @Test
    fun `check isDatabaseConfigurationValid() returns true when ran on CI`() {
        injector.isCI = true
        injector.databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
            ci = sqlite {
                path = "./build"
            }
        }
        assertEquals(true, injector.provideBuildExecutionLogic().isDatabaseConfigurationValid())
    }

    @Test
    fun `check isDatabaseConfigurationValid() returns false when ran on CI`() {
        injector.isCI = false
        injector.databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
            ci = sqlite {
                path = "./build"
            }
        }
        assertEquals(false, injector.provideBuildExecutionLogic().isDatabaseConfigurationValid())
    }

    @Test
    fun `check isDatabaseConfigurationValid() returns false when ran on Local`() {
        injector.isCI = true
        injector.databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
            local = sqlite {
                path = "./build"
            }
        }
        assertEquals(false, injector.provideBuildExecutionLogic().isDatabaseConfigurationValid())
    }

    @Test
    fun `check onExecutionFinished() returns true`() = runBlocking {
        mockkObject(GitUtils)
        every { GitUtils.currentBranch() } returns "master"

        val executedTasks = listOf<TaskInfo>()
        injector.provideBuildExecutionLogic().onExecutionFinished(executedTasks)
    }

    @Test
    fun `check onExecutionFinished() returns false when branch is not trackable`() = runBlocking {
        mockkObject(GitUtils)
        every { GitUtils.currentBranch() } returns "feature-1"

        val executedTasks = listOf<TaskInfo>()
        injector.provideBuildExecutionLogic().onExecutionFinished(executedTasks)
    }

    @Test
    fun `check onExecutionFinished() returns false when task is not trackable`() = runBlocking {
        mockkObject(GitUtils)
        every { GitUtils.currentBranch() } returns "master"
        injector.requestedTasks = listOf("clean")

        val executedTasks = listOf<TaskInfo>()
        injector.provideBuildExecutionLogic().onExecutionFinished(executedTasks)
    }

    @Test
    fun `check onExecutionFinished() returns false when database is not set`() = runBlocking {
        injector.databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig()

        val executedTasks = listOf<TaskInfo>()
        injector.provideBuildExecutionLogic().onExecutionFinished(executedTasks)
    }

    @Test
    fun `check onExecutionFinished() returns false when forbidden tasks requested`() = runBlocking {
        injector.requestedTasks = listOf("reportAnalytics")

        val executedTasks = listOf<TaskInfo>()
        injector.provideBuildExecutionLogic().onExecutionFinished(executedTasks)
    }

}