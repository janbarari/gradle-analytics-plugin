/**
 * MIT License
 * Copyright (c) 2024 Mehdi Janbarari (@janbarari)
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
package io.github.janbarari.gradle.analytics.metric.cachehit.update

import io.github.janbarari.gradle.TowerMockImpl
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.CacheHitMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ModuleCacheHit
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.extension.isNull
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateCacheHitMetricUseCaseTest {

    private lateinit var useCase: UpdateCacheHitMetricUseCase
    private lateinit var repo: DatabaseRepository

    @BeforeAll
    fun setup() {
        repo = mockk()
        useCase = UpdateCacheHitMetricUseCase(
            TowerMockImpl(),
            repo
        )
    }

    @Test
    fun `When usecase executes, expect cacheHitMetric to be returned`() = runBlocking {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "develop",
                requestedTasks = listOf("assemble"),
                createdAt = 1660202190213,
                gitHeadCommitHash = "unknown",
                modules = setOf(":core", ":data", ":domain")
            ),
            fakeBuildMetric(70, 50, 30, 20),
            fakeBuildMetric(12, 43, 11, 15),
            fakeBuildMetric(33, 89, 45, 37),
            fakeBuildMetric(99, 32, 122, 65)
        )

        every {
            repo.getTemporaryMetrics()
        } returns buildMetrics

        val result = useCase.execute()

        assertTrue {
            result?.rate == 53L &&
                    result.modules.find { it.path == ":core" }!!.rate == 53L &&
                    result.modules.find { it.path == ":data" }!!.rate == 52L &&
                    result.modules.find { it.path == ":domain" }!!.rate == 34L
        }
    }

    @Test
    fun `When usecase executes with empty modules, expect null to be returned`() = runBlocking {
        val buildMetrics = listOf(
            BuildMetric(
                branch = "develop",
                requestedTasks = listOf("assemble"),
                createdAt = 1660202190213,
                gitHeadCommitHash = "unknown",
                modules = setOf(":core", ":data", ":domain")
            ),
            fakeBuildMetric(70, 50, 30, 20),
            fakeBuildMetric(12, 43, 11, 15),
            fakeBuildMetric(33, 89, 45, 37),
            fakeBuildMetric(99, 32, 122, 65),
            BuildMetric(
                branch = "develop",
                requestedTasks = listOf("assemble"),
                createdAt = 1660202190213,
                gitHeadCommitHash = "unknown",
                modules = setOf(":core", ":data", ":domain")
            )
        )

        every {
            repo.getTemporaryMetrics()
        } returns buildMetrics

        val result = useCase.execute()

        assertTrue {
            result.isNull()
        }
    }

    private fun fakeBuildMetric(
        cacheHit: Long,
        coreCacheHit: Long,
        dataCacheHit: Long,
        domainCacheHit: Long,
    ): BuildMetric {
        return BuildMetric(
            branch = "develop",
            gitHeadCommitHash = "unknown",
            requestedTasks = listOf("assemble"),
            createdAt = 1660202190213,
            modules = setOf(":core", ":data", ":domain")
        ).apply {
            cacheHitMetric = CacheHitMetric(
                cacheHit,
                listOf(
                    ModuleCacheHit(
                        path = ":core",
                        rate = coreCacheHit
                    ),
                    ModuleCacheHit(
                        path = ":data",
                        rate = dataCacheHit
                    ),
                    ModuleCacheHit(
                        path = ":domain",
                        rate = domainCacheHit
                    )
                )
            )
        }
    }

}
