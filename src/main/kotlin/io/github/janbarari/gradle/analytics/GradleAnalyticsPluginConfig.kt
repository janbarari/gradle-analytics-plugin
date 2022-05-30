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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics

import io.github.janbarari.gradle.analytics.data.database.connection.DatabaseConnection
import io.github.janbarari.gradle.analytics.data.database.connection.MySqlDatabaseConnection
import io.github.janbarari.gradle.analytics.data.database.connection.SqliteDatabaseConnection
import org.gradle.api.Project

/**
 * Configuration options for the [io.github.janbarari.gradle.analytics.GradleAnalyticsPlugin].
 *
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
open class GradleAnalyticsPluginConfig(val project: Project) {

    companion object {
        private const val DEFAULT_MAX_TRACKING_PERIOD = 3
    }

    private var databaseConfig: DatabaseConfig = DatabaseConfig()

    var trackingTasks: List<String> = listOf()

    var trackingBranches: List<String> = listOf()

    var maxTrackingPeriod: Int = DEFAULT_MAX_TRACKING_PERIOD

    var outputPath: String = project.rootProject.buildDir.absolutePath

    fun database(block: DatabaseConfig.() -> Unit) {
        databaseConfig = DatabaseConfig().also(block)
    }

    fun getDatabaseConfig(): DatabaseConfig = databaseConfig

    class DatabaseConfig : java.io.Serializable {

        /**
         * It is the database config of user local machine, this variable should be initialized
         * with one of the database configs that the plugin supports.
         */
        var local: DatabaseConnection? = null

        /**
         * It is the database config of CI. Should be initialized
         * with one of the database configs that the plugin supports. Keep in mind that
         * if this variable is initialized, then the plugin only uses this database
         * config on CI.
         *
         * Note: please make sure the CI has an environment variable named `CI`.
         */
        var ci: DatabaseConnection? = null

        /**
         * Factory method for create a new instance
         * of [io.github.janbarari.gradle.analytics.data.database.config.MySqlDatabaseConfig].
         */
        fun mysql(block: MySqlDatabaseConnection.() -> Unit): MySqlDatabaseConnection {
            return MySqlDatabaseConnection {
                also(block)
            }
        }

        /**
         * Factory method for create a new instance
         * of [io.github.janbarari.gradle.analytics.data.database.config.SqliteDatabaseConfig].
         */
        fun sqlite(block: SqliteDatabaseConnection.() -> Unit): SqliteDatabaseConnection {
            return SqliteDatabaseConnection {
                also(block)
            }
        }

    }

}
