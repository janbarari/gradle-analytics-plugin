package io.github.janbarari.gradle.analytics.scanner

import io.github.janbarari.gradle.analytics.DatabaseConfig
import io.github.janbarari.gradle.analytics.database.SqliteDatabaseConnection
import io.github.janbarari.gradle.analytics.domain.model.ModulesDependencyGraph
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
        databaseConfig = DatabaseConfig().apply {
            local = SqliteDatabaseConnection {
                path = "./build"
                name = "testdb"
            }
        },
        isCI = false,
        branch = "master",
        requestedTasks = listOf("assembleDebug"),
        trackingTasks = listOf("assembleDebug"),
        modules = emptyList(),
        modulesDependencyGraph = ModulesDependencyGraph(dependencies = emptyList()),
        nonCacheableTasks = emptyList()
    )

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
        injector.databaseConfig = DatabaseConfig()

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