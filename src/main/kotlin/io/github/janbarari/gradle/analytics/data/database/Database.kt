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

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.janbarari.gradle.analytics.data.database.config.DatabaseConfig
import io.github.janbarari.gradle.analytics.data.database.config.MySqlDatabaseConfig
import io.github.janbarari.gradle.analytics.data.database.config.SqliteDatabaseConfig
import io.github.janbarari.gradle.analytics.data.database.table.MysqlDailyBuildTable
import io.github.janbarari.gradle.analytics.data.database.table.SqliteDailyBuildTable
import io.github.janbarari.gradle.analytics.domain.metric.BuildMetric
import io.github.janbarari.gradle.analytics.plugin.configuration.DatabaseExtension
import io.github.janbarari.gradle.utils.Clock
import io.github.janbarari.gradle.utils.isNotNull
import io.github.janbarari.gradle.utils.isNull
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
    private var moshi: Moshi
    private var jsonAdapter: JsonAdapter<BuildMetric>

    init {
        connect(config)
        moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        jsonAdapter = moshi.adapter(BuildMetric::class.java)
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

    private fun isConnected(): Boolean {
        return this::_database.isInitialized
    }

    fun saveNewMetric(metric: BuildMetric): Boolean {
        if (!isConnected()) return false

        return transaction {

            if (databaseConfig is MySqlDatabaseConfig) {

                val queryResult = MysqlDailyBuildTable.insert {
                    it[createdAt] = System.currentTimeMillis()
                    it[value] = jsonAdapter.toJson(metric)
                }
                return@transaction queryResult.insertedCount == 1

            }

            if (databaseConfig is SqliteDatabaseConfig) {

                val queryResult = SqliteDailyBuildTable.insert {
                    it[MysqlDailyBuildTable.createdAt] = System.currentTimeMillis()
                    it[MysqlDailyBuildTable.value] = jsonAdapter.toJson(metric)
                }
                return@transaction queryResult.insertedCount == 1

            }

            return@transaction false

        }
    }

    fun isTodayMetricExists(): Boolean {
        if (!isConnected()) return false

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

    fun getTodayMetric(): Pair<BuildMetric, Long>? {
        if (!isConnected()) return null

        return transaction {

            if (databaseConfig is MySqlDatabaseConfig) {
                val queryResult = MysqlDailyBuildTable.select {
                    MysqlDailyBuildTable.createdAt greaterEq Clock.getCurrentDayMillis()
                }.single()
                return@transaction Pair(
                    jsonAdapter.fromJson(queryResult[MysqlDailyBuildTable.value])!!,
                    queryResult[MysqlDailyBuildTable.number]
                )
            }

            if (databaseConfig is SqliteDatabaseConfig) {
                val queryResult = SqliteDailyBuildTable.select {
                    SqliteDailyBuildTable.createdAt greaterEq Clock.getCurrentDayMillis()
                }.single()
                return@transaction Pair(
                    jsonAdapter.fromJson(queryResult[SqliteDailyBuildTable.value])!!,
                    queryResult[SqliteDailyBuildTable.number]
                )
            }

            return@transaction null

        }
    }

    fun updateExistingMetric(number: Long, metric: BuildMetric): Boolean {
        return transaction {
            when(databaseConfig) {
                is MySqlDatabaseConfig -> {
                    val queryResult = MysqlDailyBuildTable.update({
                        MysqlDailyBuildTable.number eq number
                    }) {
                        it[value] = jsonAdapter.toJson(metric)
                    }
                    return@transaction queryResult == 1
                }

                is SqliteDatabaseConfig -> {
                    val queryResult = SqliteDailyBuildTable.update({
                        SqliteDailyBuildTable.number eq number
                    }) {
                        it[value] = jsonAdapter.toJson(metric)
                    }
                    return@transaction queryResult == 1
                }

            }
            return@transaction false
        }
    }

}
