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

    @BeforeAll
    fun setup() {
        usecase = CreateCacheHitMetricUseCase()
    }

    @Test
    fun `Check cache hit metric generates`() = runBlocking {
        val modules = listOf(
            ModulePath(path = ":app", "TEMPORARY_DIRECTORY"),
            ModulePath(path = ":domain", "TEMPORARY_DIRECTORY"),
            ModulePath(path = ":core", "TEMPORARY_DIRECTORY")
        )

        val tasks = listOf(
            fakeTask(":app", cache = true),
            fakeTask(":core", cache = true),
            fakeTask(":domain", cache = true),
            fakeTask(":app", cache = false),
            fakeTask(":core", cache = false),
            fakeTask(":domain", cache = true),
            fakeTask(":app", cache = false),
            fakeTask(":core", cache = false),
            fakeTask(":domain", cache = false),
            fakeTask(":domain", cache = true, isSkipped = true),
            fakeTask(":app", cache = false, isSkipped = true),
        )

        val cacheHitMetric = usecase.execute(modules to tasks)

        println(cacheHitMetric)

        assertTrue {
            cacheHitMetric.rate == 33L
        }

        assertTrue {
            cacheHitMetric.modules[0].rate == 0L
        }
        assertTrue {
            cacheHitMetric.modules[1].rate == 50L
        }
        assertTrue {
            cacheHitMetric.modules[2].rate == 33L
        }
    }

    private fun fakeTask(modulePath: String, cache: Boolean, isSkipped: Boolean = false): TaskInfo {
        return TaskInfo(
            startedAt = 0,
            finishedAt = 0,
            path = "$modulePath:fakeTask",
            displayName = "Fake task",
            name = "Fake task",
            isSuccessful = true,
            failures = null,
            dependencies = null,
            isIncremental = cache,
            isFromCache = cache,
            isUpToDate = cache,
            isSkipped = isSkipped,
            executionReasons = null
        )
    }

}
