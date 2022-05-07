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

import io.github.janbarari.gradle.analytics.domain.entity.Build
import io.github.janbarari.gradle.analytics.domain.entity.Task
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
class SQLiteDatabase {

    private var config: DatabaseConfig

    constructor(config: DatabaseConfig) {
        this.config = config
        connect()
        createEntities(Build, Task)
    }

    /**
     * Opens a connection to the SQLite local or remote database.
     */
    private fun connect() {
        config.ensureRequiredInputsExist()
        Database.connect(
            url = "jdbc:sqlite:${config.url}",
            driver = "org.sqlite.JDBC",
            user = config.user,
            password = config.password
        )
    }

    /**
     * Creates the database entities if not exist.
     */
    private fun createEntities(vararg entities: Table) {
        transaction {
            if (config.isQueryLogEnabled) {
                addLogger(StdOutSqlLogger)
            }
            SchemaUtils.create(*entities)
        }
    }

}
