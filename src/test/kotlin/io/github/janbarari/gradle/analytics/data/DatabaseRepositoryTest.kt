package io.github.janbarari.gradle.analytics.data

import com.squareup.moshi.Moshi
import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import io.github.janbarari.gradle.analytics.data.database.Database
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseRepositoryTest {

    private lateinit var repo: DatabaseRepository

    @BeforeAll
    fun setup() {
        val databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
            local = sqlite {
                path = "./build"
                name = "testdb"
            }
        }
        val db = Database(databaseConfig, false)
        repo = DatabaseRepositoryImp(
            db = db,
            branch = "develop",
            requestedTasks = "assembleDebug",
            moshi = Moshi.Builder().build()
        )
    }

    @Test
    fun `check saveNewMetric() returns true when successful`() {
        val metric = BuildMetric(
            branch = "develop",
            listOf("assembleDebug"),
            createdAt = 16194745374333,
            gitHeadCommitHash = "unknown",
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
    fun `check isDayMetricExists() returns true when data is exists`() {
        val metric = BuildMetric(
            branch = "develop",
            listOf("assembleDebug"),
            createdAt = System.currentTimeMillis(),
            gitHeadCommitHash = "unknown",
        )
        repo.saveNewMetric(metric)
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
        )
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
            )
            repo.saveNewMetric(metric)
        }
        val dayMetric = repo.getDayMetric()
        val newMetric = BuildMetric(
            branch = "master",
            listOf("assembleRelease"),
            createdAt = System.currentTimeMillis(),
            gitHeadCommitHash = "unknown",
        )
        repo.updateDayMetric(dayMetric.second, newMetric)
        assertEquals("master", repo.getDayMetric().first.branch)
    }

    @Test
    fun `check getMetrics() returns correct result`() {
        assert(repo.getMetrics(3L to 3L) is List<BuildMetric>)
    }

}