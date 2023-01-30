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
package io.github.janbarari.gradle.analytics.data

import com.squareup.moshi.Moshi
import io.github.janbarari.gradle.TowerMockImpl
import io.github.janbarari.gradle.analytics.DatabaseConfig
import io.github.janbarari.gradle.analytics.database.Database
import io.github.janbarari.gradle.analytics.database.DatabaseResultMigrationPipeline
import io.github.janbarari.gradle.analytics.database.SqliteDatabaseConnection
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetricJsonAdapter
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseRepositoryTest {

    private lateinit var repo: DatabaseRepository
    private var databaseResultMigrationPipeline: DatabaseResultMigrationPipeline = mockk()

    @BeforeAll
    fun setup() {
        val databaseConfig = DatabaseConfig().apply {
            local = SqliteDatabaseConnection {
                path = "./build"
                name = "testdb"
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
            temporaryMetricsMemoryCache = TemporaryMetricsMemoryCacheImpl(TowerMockImpl()),
            databaseResultMigrationPipeline = databaseResultMigrationPipeline
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
    fun `check isDayMetricExists() returns true when data is exists`() = runBlocking {
        val metric = BuildMetric(
            branch = "develop",
            listOf("assembleDebug"),
            createdAt = System.currentTimeMillis(),
            gitHeadCommitHash = "unknown",
            modules = emptySet()
        )
        repo.saveNewMetric(metric)
        delay(300)
        assertEquals(true, repo.isDayMetricExists())
    }

    @Test
    fun `check getDayMetric() returns result when data is exists`() {
        repo.dropMetrics()
        val metric = BuildMetric(
            branch = "develop",
            listOf("assembleDebug"),
            createdAt = System.currentTimeMillis(),
            gitHeadCommitHash = "unknown",
            modules = emptySet()
        )

        every {
            databaseResultMigrationPipeline.execute(any())
        } returns metric

        repo.saveNewMetric(metric)
        assertEquals("develop", repo.getDayMetric().first.branch)
        assertEquals("assembleDebug", repo.getDayMetric().first.requestedTasks.first())
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
    fun `check updateDayMetric() updates the day metric`() {
        if (!repo.isDayMetricExists()) {
            val metric = BuildMetric(
                branch = "develop",
                listOf("assembleDebug"),
                createdAt = System.currentTimeMillis(),
                gitHeadCommitHash = "unknown",
                modules = emptySet()
            )
            repo.saveNewMetric(metric)
        }
        val dayMetric = repo.getDayMetric()
        val newMetric = BuildMetric(
            branch = "master",
            listOf("assembleRelease"),
            createdAt = System.currentTimeMillis(),
            gitHeadCommitHash = "unknown",
            modules = emptySet()
        )
        every {
            databaseResultMigrationPipeline.execute(any())
        } returns newMetric

        repo.updateDayMetric(dayMetric.second, newMetric)
        assertEquals("master", repo.getDayMetric().first.branch)
    }

    @Test
    fun `check getMetrics() returns correct result`() {
        assert(repo.getMetrics(3L to 3L) is List<BuildMetric>)
    }

}