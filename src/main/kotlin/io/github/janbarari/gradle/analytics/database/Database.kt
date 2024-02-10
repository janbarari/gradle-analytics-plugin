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
package io.github.janbarari.gradle.analytics.database

import io.github.janbarari.gradle.analytics.database.table.MetricTable
import io.github.janbarari.gradle.analytics.database.table.TemporaryMetricTable
import io.github.janbarari.gradle.ExcludeJacocoGenerated
import io.github.janbarari.gradle.analytics.DatabaseConfig
import io.github.janbarari.gradle.analytics.database.table.SingleMetricTable
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.toRealPath
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.logger.Tower
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transactionManager

class Database(
    private val tower: Tower,
    config: DatabaseConfig,
    private var isCI: Boolean
) {

    companion object {
        const val DEFAULT_VARCHAR_LENGTH = 256
        private val clazz = io.github.janbarari.gradle.analytics.database.Database::class.java
    }

    private lateinit var _database: Database
    private var databaseConfig: DatabaseConnection? = null

    init {
        connect(config)
    }

    private fun connect(config: DatabaseConfig) {
        tower.i(clazz, "connect()")
        databaseConfig = config.local

        if (isCI && config.ci.isNotNull()) {
            databaseConfig = config.ci
        }

        databaseConfig.whenNotNull {

            when (databaseConfig) {
                is MySqlDatabaseConnection -> {
                    LongTextColumnType.longTextType = LongTextColumnType.Companion.LongTextType.MEDIUMTEXT
                    connectMysqlDatabase(databaseConfig as MySqlDatabaseConnection)
                    ResetAutoIncremental.dbType = MySqlDatabaseConnection::class.java
                }
                is SqliteDatabaseConnection -> {
                    LongTextColumnType.longTextType = LongTextColumnType.Companion.LongTextType.TEXT
                    connectSqliteDatabase(databaseConfig as SqliteDatabaseConnection)
                    ResetAutoIncremental.dbType = SqliteDatabaseConnection::class.java
                }
                is PostgresDatabaseConnection -> {
                    LongTextColumnType.longTextType = LongTextColumnType.Companion.LongTextType.TEXT
                    connectPostgresDatabase(databaseConfig as PostgresDatabaseConnection)
                    ResetAutoIncremental.dbType = PostgresDatabaseConnection::class.java
                }
            }

            createTables(MetricTable, TemporaryMetricTable, SingleMetricTable)
        }
    }

    private fun connectMysqlDatabase(config: MySqlDatabaseConnection) {
        tower.i(clazz, "connectToMysqlDatabase()")
        _database = Database.connect(
            url = "jdbc:mysql://${config.host}:${config.port}/${config.name}",
            driver = "com.mysql.cj.jdbc.Driver",
            user = config.user,
            password = config.password
        )
    }

    private fun connectSqliteDatabase(config: SqliteDatabaseConnection) {
        tower.i(clazz, "connectSqliteDatabase()")
        _database = Database.connect(
            url = "jdbc:sqlite:${config.path!!.toRealPath()}/${config.name}.db",
            driver = "org.sqlite.JDBC",
            user = config.user,
            password = config.password
        )
    }

    private fun connectPostgresDatabase(config: PostgresDatabaseConnection) {
        tower.i(clazz, "connectPostgresDatabase()")
        _database = Database.connect(
            url = "jdbc:postgresql://${config.host}:${config.port}/${config.name}",
            driver = "org.postgresql.Driver",
            user = config.user,
            password = config.password
        )
    }

    /**
     * Creates the database tables if not exist.
     */
    private fun createTables(vararg entities: Table) {
        tower.i(clazz, "createTables() with ${entities.size} tables")
        transaction  {
            SchemaUtils.createMissingTablesAndColumns(*entities, withLogs = false)
        }
    }

    @ExcludeJacocoGenerated
    fun <T> transaction(statement: Transaction.() -> T): T {
        return transaction(
            transactionIsolation = _database.transactionManager.defaultIsolationLevel,
            readOnly = false,
            db = _database,
            statement = statement
        )
    }

}
