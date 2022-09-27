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

import groovy.lang.Closure
import io.github.janbarari.gradle.ExcludeJacocoGenerated
import io.github.janbarari.gradle.analytics.database.DatabaseConnection
import io.github.janbarari.gradle.analytics.database.MySqlDatabaseConnection
import io.github.janbarari.gradle.analytics.database.SqliteDatabaseConnection

@ExcludeJacocoGenerated
class DatabaseConfig @JvmOverloads constructor() : java.io.Serializable {

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
     * of [io.github.janbarari.gradle.analytics.data.database.config.MySqlDatabaseConfig].
     */
    fun mysql(closure: Closure<*>): MySqlDatabaseConnection {
        val temp = MySqlDatabaseConnection { }
        closure.delegate = temp
        closure.call()
        return temp
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

    /**
     * Factory method for create a new instance
     * of [io.github.janbarari.gradle.analytics.data.database.config.SqliteDatabaseConfig].
     */
    fun sqlite(closure: Closure<*>): SqliteDatabaseConnection {
        val temp = SqliteDatabaseConnection { }
        closure.delegate = temp
        closure.call()
        return temp
    }


}
