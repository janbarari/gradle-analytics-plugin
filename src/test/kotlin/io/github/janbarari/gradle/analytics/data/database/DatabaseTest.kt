package io.github.janbarari.gradle.analytics.data.database

import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class DatabaseTest {

    @Test
    fun `check the database creates successfully with isQueryLogEnabled=true`() {

        val databaseExtension = GradleAnalyticsPluginConfig.DatabaseConfig()
        databaseExtension.apply {
            local = sqlite {
                path = "/Users/workstation/workstation/github/janbarari/satellitestracker"
            }
        }
        runBlocking {
            val db = Database(databaseExtension, false)
        }

    }

}