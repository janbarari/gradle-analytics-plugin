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
package io.github.janbarari.gradle.analytics.domain.model

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
            branch = "master",
            gitHeadCommitHash = "unknown",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(0, info.createdAt)
        assertEquals(0, info.startedAt)
        assertEquals(50, info.initializedAt)
        assertEquals(150, info.configuredAt)
        assertEquals(0, info.dependenciesResolveInfo.size)
        assertEquals(0, info.executedTasks.size)
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
            configuredAt = 250,
            dependenciesResolveInfo = listOf(),
            executedTasks = listOf(),
            finishedAt = 800,
            branch = "master",
            gitHeadCommitHash = "unknown",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(200, info.getTotalDuration().toMillis())
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
            branch = "master",
            gitHeadCommitHash = "unknown",
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
            branch = "master",
            gitHeadCommitHash = "unknown",
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
            branch = "master",
            gitHeadCommitHash = "unknown",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(100, info.getConfigurationDuration().toMillis())
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
            branch = "master",
            gitHeadCommitHash = "unknown",
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
            executedTasks = listOf(
                TaskInfo(
                    startedAt = 100,
                    finishedAt = 200,
                    path = "assemble",
                    displayName = "Assemble",
                    name = "assemble",
                    isSuccessful = true,
                    failures = null,
                    dependencies = null,
                    isIncremental = false,
                    isFromCache = false,
                    isUpToDate = false,
                    isSkipped = false,
                    executionReasons = null
                )
            ),
            finishedAt = 800,
            branch = "master",
            gitHeadCommitHash = "unknown",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(100, info.getExecutionDuration().toMillis())
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
            branch = "master",
            gitHeadCommitHash = "unknown",
            requestedTasks = listOf(),
            isSuccessful = true
        )
        assertEquals(0, info.getTotalDependenciesResolveDuration().toMillis())
    }

}