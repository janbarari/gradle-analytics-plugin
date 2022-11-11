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

import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.CacheHitMetric
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.isNull
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateCacheHitMetricStageTest {

    private lateinit var useCase: CreateCacheHitMetricUseCase

    private val buildMetric = BuildMetric(
        branch = "develop",
        requestedTasks = listOf("assemble"),
        createdAt = 16588904332,
        gitHeadCommitHash = "unknown"
    )

    private var buildInfo = BuildInfo(
        createdAt = 16588904332,
        startedAt = 16588904332,
        initializedAt = 16588908332,
        configuredAt = 16588990123,
        dependenciesResolveInfo = emptyList(),
        executedTasks = emptyList(),
        finishedAt = 16589012344,
        branch = "develop",
        gitHeadCommitHash = "unknown",
        requestedTasks = listOf("assemble"),
        isSuccessful = true
    )

    @BeforeAll
    fun setup() {
        useCase = mockk()
        coEvery {
            useCase.execute(any())
        } returns CacheHitMetric(rate = 34, modules = emptyList())
    }

    @Test
    fun `When the stage proceeds with successful build, expect cacheHitMetric to be generated`() = runBlocking {
        val buildInfo = buildInfo.copy(isSuccessful = true)
        val buildMetric = buildMetric.copy()
        val stage = CreateCacheHitMetricStage(buildInfo, useCase)

        stage.process(buildMetric)

        assertTrue {
            buildMetric.cacheHitMetric.isNotNull() && buildMetric.cacheHitMetric?.rate == 34L
        }
    }

    @Test
    fun `When the stage proceeds with failed build, expect to cacheHitMetric generation be skipped`() = runBlocking {
        val buildInfo = buildInfo.copy(isSuccessful = false)
        val buildMetric = buildMetric.copy()

        val stage = CreateCacheHitMetricStage(buildInfo, useCase)

        stage.process(buildMetric)

        assertTrue {
            buildMetric.cacheHitMetric.isNull()
        }
    }

}
