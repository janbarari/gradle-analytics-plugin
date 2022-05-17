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

import io.github.janbarari.gradle.analytics.data.database.config.DatabaseConfig
import io.github.janbarari.gradle.analytics.data.database.config.MySqlDatabaseConfig
import io.github.janbarari.gradle.analytics.data.database.config.SqliteDatabaseConfig
import io.github.janbarari.gradle.analytics.domain.entity.MysqlDailyBuildTable
import io.github.janbarari.gradle.analytics.domain.entity.SqliteDailyBuildTable
import io.github.janbarari.gradle.analytics.extension.DatabaseExtension
import io.github.janbarari.gradle.utils.Clock
import io.github.janbarari.gradle.utils.isNotNull
import io.github.janbarari.gradle.utils.isNull
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
class Database(
    config: DatabaseExtension,
    private var isCI: Boolean
) {

    private lateinit var _database: org.jetbrains.exposed.sql.Database
    private var databaseConfig: DatabaseConfig? = null

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
            is MySqlDatabaseConfig -> {
                connectToMysqlDatabase(databaseConfig as MySqlDatabaseConfig)
                createTables(MysqlDailyBuildTable)
            }
            is SqliteDatabaseConfig -> {
                connectSqliteDatabase(databaseConfig as SqliteDatabaseConfig)
                createTables(SqliteDailyBuildTable)
            }
        }
    }

    private fun connectToMysqlDatabase(config: MySqlDatabaseConfig) {
        _database = org.jetbrains.exposed.sql.Database.connect(
            url = "jdbc:mysql://${config.hostIp}:${config.port}/${config.name}",
            driver = "com.mysql.cj.jdbc.Driver",
            user = config.user,
            password = config.password
        )
    }

    private fun connectSqliteDatabase(config: SqliteDatabaseConfig) {
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

    fun insertBuild(jsonValue: String) {
        if (this::_database.isInitialized.not()) {
            return
        }
        transaction {

            if (databaseConfig is MySqlDatabaseConfig) {
                MysqlDailyBuildTable.insert {
                    it[createdAt] = System.currentTimeMillis()
                    it[value] = jsonValue
                }
            }

            if (databaseConfig is SqliteDatabaseConfig) {
                SqliteDailyBuildTable.insert {
                    it[MysqlDailyBuildTable.createdAt] = System.currentTimeMillis()
                    it[MysqlDailyBuildTable.value] = jsonValue
                }
            }

        }
    }

    fun isTodayBuildAdded(): Boolean {
        if (this::_database.isInitialized.not()) {
            return false
        }
        return transaction {
            if (databaseConfig is MySqlDatabaseConfig) {
                val queryResult = MysqlDailyBuildTable.select {
                    MysqlDailyBuildTable.createdAt greaterEq Clock.getCurrentDayMillis()
                }
                return@transaction queryResult.count() > 0
            }
            if (databaseConfig is SqliteDatabaseConfig) {
                val queryResult = SqliteDailyBuildTable.select {
                    SqliteDailyBuildTable.createdAt greaterEq Clock.getCurrentDayMillis()
                }
                return@transaction queryResult.count() > 0
            }
            return@transaction false
        }
    }

}
