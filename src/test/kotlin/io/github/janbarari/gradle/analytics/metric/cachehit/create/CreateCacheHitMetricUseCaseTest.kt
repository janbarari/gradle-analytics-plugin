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
package io.github.janbarari.gradle.analytics.metric.cachehit.create

import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateCacheHitMetricUseCaseTest {

    lateinit var usecase: CreateCacheHitMetricUseCase

    private val modules = listOf(
        ModulePath(path = ":app", "TEMPORARY_DIRECTORY"),
        ModulePath(path = ":domain", "TEMPORARY_DIRECTORY"),
        ModulePath(path = ":core", "TEMPORARY_DIRECTORY")
    )

    private val tasks = listOf(
        fakeTask(":app", isFromCache = true, isUpToDate = false, isSkipped = false),
        fakeTask(":core", isFromCache = true, isUpToDate = false, isSkipped = false),
        fakeTask(":domain", isFromCache = true, isUpToDate = false, isSkipped = false),
        fakeTask(":app", isFromCache = true, isUpToDate = false, isSkipped = false),
        fakeTask(":core", isFromCache = true, isUpToDate = false, isSkipped = false),
        fakeTask(":domain", isFromCache = true, isUpToDate = false, isSkipped = false),
        fakeTask(":app", isFromCache = true, isUpToDate = false, isSkipped = false),
        fakeTask(":core", isFromCache = false, isUpToDate = false, isSkipped = false),
        fakeTask(":domain", isFromCache = true, isUpToDate = false, isSkipped = false),
        fakeTask(":domain", isFromCache = true, isUpToDate = true, isSkipped = false),
        fakeTask(":app", isFromCache = true, isUpToDate = false, isSkipped = true)
    )

    @BeforeAll
    fun setup() {
        usecase = CreateCacheHitMetricUseCase()
    }

    @Test
    fun `When CacheHitMetricUseCase executes, expect cacheHitMetric`() = runBlocking {
        val cacheHitMetric = usecase.execute(input = modules to tasks)

        assertTrue {
            cacheHitMetric.rate == 90L &&
                    cacheHitMetric.modules[0].rate == 75L &&
                    cacheHitMetric.modules[1].rate == 100L &&
                    cacheHitMetric.modules[2].rate == 66L
        }
    }

    private fun fakeTask(modulePath: String,
                         isFromCache: Boolean,
                         isUpToDate: Boolean,
                         isSkipped: Boolean): TaskInfo {
        return TaskInfo(
            startedAt = 16578948883,
            finishedAt = 16578948883,
            path = "$modulePath:fakeTask",
            displayName = "Fake task",
            name = "Fake task",
            isSuccessful = true,
            failures = null,
            dependencies = null,
            isIncremental = isFromCache || isUpToDate,
            isFromCache = isFromCache,
            isUpToDate = isUpToDate,
            isSkipped = isSkipped,
            executionReasons = null
        )
    }

}
