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
package io.github.janbarari.gradle.analytics.scanner

import io.github.janbarari.gradle.MockListProperty
import io.github.janbarari.gradle.MockProperty
import io.github.janbarari.gradle.MockSetProperty
import io.github.janbarari.gradle.analytics.DatabaseConfig
import io.github.janbarari.gradle.analytics.database.SqliteDatabaseConnection
import io.github.janbarari.gradle.analytics.domain.model.Module
import io.github.janbarari.gradle.analytics.domain.model.ModulesDependencyGraph
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionInjector
import io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionService
import io.github.janbarari.gradle.analytics.scanner.execution.provideBuildExecutionLogic
import io.github.janbarari.gradle.utils.GitUtils
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.junit.jupiter.api.Test

class BuildExecutionLogicTest {

    private var injector = BuildExecutionInjector(
        object :BuildExecutionService.Params {
            override val enabled: Property<Boolean>
                get() = MockProperty(true)
            override val databaseConfig: Property<DatabaseConfig>
                get() = MockProperty(DatabaseConfig().apply {
                    local = SqliteDatabaseConnection {
                        path = "./build"
                        name = "testdb"
                    }
                })
            override val envCI: Property<Boolean>
                get() = MockProperty(false)
            override val requestedTasks: ListProperty<String>
                get() = MockListProperty(mutableListOf("assembleDebug"))
            override val trackingTasks: SetProperty<String>
                get() = MockSetProperty(mutableSetOf("assembleDebug"))
            override val trackingBranches: SetProperty<String>
                get() = MockSetProperty(mutableSetOf("master"))
            override val trackAllBranchesEnabled: Property<Boolean>
                get() = MockProperty(false)
            override val modules: SetProperty<Module>
                get() = MockSetProperty(mutableSetOf())
            override val modulesDependencyGraph: Property<ModulesDependencyGraph>
                get() = MockProperty(ModulesDependencyGraph(dependencies = emptyList()))
            override val nonCacheableTasks: SetProperty<String>
                get() = MockSetProperty(mutableSetOf())
            override val outputPath: Property<String> = MockProperty("./build")
            override val maximumWorkerCount: Property<Int>
                get() = MockProperty(0)
        }
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
        injector.parameters.requestedTasks.set(listOf("clean"))

        val executedTasks = listOf<TaskInfo>()
        injector.provideBuildExecutionLogic().onExecutionFinished(executedTasks)
    }

    @Test
    fun `check onExecutionFinished() returns false when forbidden tasks requested`() = runBlocking {
        injector.parameters.requestedTasks.set(listOf("reportAnalytics"))

        val executedTasks = listOf<TaskInfo>()
        injector.provideBuildExecutionLogic().onExecutionFinished(executedTasks)
    }

}