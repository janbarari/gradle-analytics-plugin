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

import io.github.janbarari.gradle.analytics.data.database.exception.DatabaseConfigNotDefinedException

class DatabaseConfig {

    /**
     * The SQLite server URL or local database file path, It will create the local
     * database file if the database is not exists.
     *
     * Required!
     *
     * local database path example: "/build/temporary.db"
     */
    lateinit var url: String

    /**
     * Database username.
     *
     * Required!
     */
    lateinit var user: String

    /**
     * Database user password.
     */
    var password: String = ""

    /**
     * Enables or disables the database query logs.
     */
    var isQueryLogEnabled: Boolean = false

    /**
     * Ensures the required inputs are exists.
     * @throws DatabaseConfigNotDefinedException If the required inputs are not exist.
     */
    @kotlin.jvm.Throws(DatabaseConfigNotDefinedException::class)
    fun ensureRequiredInputsExist(): Boolean {
        if (this::url.isInitialized.not() ||
                this::user.isInitialized.not()) {
            throw DatabaseConfigNotDefinedException()
        }
        return true
    }

}
