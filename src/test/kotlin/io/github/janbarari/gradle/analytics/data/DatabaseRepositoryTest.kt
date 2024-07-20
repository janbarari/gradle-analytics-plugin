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
package io.github.janbarari.gradle.analytics.data

import com.squareup.moshi.Moshi
import io.github.janbarari.gradle.TowerMockImpl
import io.github.janbarari.gradle.analytics.DatabaseConfig
import io.github.janbarari.gradle.analytics.database.Database
import io.github.janbarari.gradle.analytics.database.SqliteDatabaseConnection
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetricJsonAdapter
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.random.Random
import kotlin.test.assertEquals

//todo these tests are deprecated
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseRepositoryTest {

    private lateinit var repo: DatabaseRepository

    @BeforeEach
    fun setup() {
        val databaseConfig = DatabaseConfig().apply {
            local = SqliteDatabaseConnection {
                path = "./build"
                name = "DRT_${Random.nextInt()}"
            }
        }
        val db = Database(
            TowerMockImpl(),
            databaseConfig,
            false
        )

        repo = DatabaseRepositoryImp(
            tower = TowerMockImpl(),
            db = db,
            branch = "develop",
            requestedTasks = "assembleDebug",
            buildMetricJsonAdapter = BuildMetricJsonAdapter(Moshi.Builder().build()),
            temporaryMetricsMemoryCache = TemporaryMetricsMemoryCacheImpl(TowerMockImpl())
        )
    }

    @Test
    fun `check saveNewMetric() returns true when successful`() {
        val metric = BuildMetric(
            branch = "develop",
            listOf("assembleDebug"),
            createdAt = 16194745374333,
            gitHeadCommitHash = "unknown",
            modules = emptySet()
        )
        assertDoesNotThrow {
            repo.saveNewMetric(metric)
        }
    }

    @Test
    fun `check saveTemporaryMetric() returns true when successful`() {
        val metric = BuildMetric(
            branch = "develop",
            listOf("assembleDebug"),
            createdAt = 16194745374333,
            gitHeadCommitHash = "unknown",
            modules = emptySet()
        )
        assertDoesNotThrow {
            repo.saveTemporaryMetric(metric)
        }
    }

    @Test
    fun `check isDayMetricExists() returns false when data is empty`() {
        repo.dropMetrics()
        assertEquals(false, repo.isDayMetricExists())
    }

    @Test
    fun `check dropOutdatedTemporaryMetrics() returns true`() {
        repo.dropOutdatedTemporaryMetrics()
    }

    @Test
    fun `check getTemporaryMetrics() returns correct result`() {
        assert(repo.getTemporaryMetrics() is List<BuildMetric>)
    }

    @Test
    fun `check getMetrics() returns correct result`() {
        assert(repo.getMetrics(3L to 3L) is List<BuildMetric>)
    }

}