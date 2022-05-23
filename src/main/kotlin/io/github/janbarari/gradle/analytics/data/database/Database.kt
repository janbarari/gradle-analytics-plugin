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
package io.github.janbarari.gradle.analytics.data.database

import io.github.janbarari.gradle.analytics.configuration.MySqlDatabase
import io.github.janbarari.gradle.analytics.configuration.SqliteDatabase
import io.github.janbarari.gradle.analytics.data.database.table.MetricTable
import io.github.janbarari.gradle.analytics.data.database.table.TemporaryMetricTable
import io.github.janbarari.gradle.analytics.configuration.DatabaseExtension
import io.github.janbarari.gradle.utils.isNotNull
import io.github.janbarari.gradle.utils.isNull
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transactionManager

/**
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
@SuppressWarnings("WildcardImport")
class Database(
    config: DatabaseExtension,
    private var isCI: Boolean
) {

    private lateinit var _database: Database
    private var databaseConfig: io.github.janbarari.gradle.analytics.configuration.Database? = null

    init {
        connect(config)
    }

    private fun connect(config: DatabaseExtension) {
        databaseConfig = config.local

        if (isCI && config.ci.isNotNull()) {
            databaseConfig = config.ci
        }

        if (databaseConfig.isNull()) {
            return
        }

        when (databaseConfig) {
            is MySqlDatabase -> {
                LongTextColumnType.longTextType = LongTextColumnType.Companion.LongTextType.MEDIUMTEXT
                connectToMysqlDatabase(databaseConfig as MySqlDatabase)
            }
            is SqliteDatabase -> {
                LongTextColumnType.longTextType = LongTextColumnType.Companion.LongTextType.TEXT
                connectSqliteDatabase(databaseConfig as SqliteDatabase)
            }
        }

        createTables(MetricTable, TemporaryMetricTable)
    }

    private fun connectToMysqlDatabase(config: MySqlDatabase) {
        _database = org.jetbrains.exposed.sql.Database.connect(
            url = "jdbc:mysql://${config.hostIp}:${config.port}/${config.name}",
            driver = "com.mysql.cj.jdbc.Driver",
            user = config.user,
            password = config.password
        )
    }

    private fun connectSqliteDatabase(config: SqliteDatabase) {
        _database = org.jetbrains.exposed.sql.Database.connect(
            url = "jdbc:sqlite:${config.path}/${config.name}.db",
            driver = "org.sqlite.JDBC",
            user = config.user,
            password = config.password
        )
    }

    /**
     * Creates the database tables if not exist.
     */
    private fun createTables(vararg entities: Table) {
        transaction {
            if (databaseConfig!!.isQueryLogEnabled) {
                addLogger(StdOutSqlLogger)
            }
            SchemaUtils.createMissingTablesAndColumns(*entities, withLogs = true)
        }
    }

    fun isConnected(): Boolean {
        return this::_database.isInitialized
    }

    fun <T> transaction(statement: Transaction.() -> T): T {
        return transaction(
            _database.transactionManager.defaultIsolationLevel,
            _database.transactionManager.defaultRepetitionAttempts,
            _database,
            statement
        )
    }

}
