package io.github.janbarari.gradle.analytics.data.database

import io.github.janbarari.gradle.analytics.data.database.config.MySqlDatabaseConfig
import io.github.janbarari.gradle.analytics.data.database.config.SqliteDatabaseConfig
import io.github.janbarari.gradle.analytics.extension.DatabaseExtension
import org.junit.jupiter.api.Test

class DatabaseTest {

    @Test
    fun `check the database creates successfully with isQueryLogEnabled=true`() {

        DatabaseExtension().apply {
            local = mysql {

            }
        }
    }

}