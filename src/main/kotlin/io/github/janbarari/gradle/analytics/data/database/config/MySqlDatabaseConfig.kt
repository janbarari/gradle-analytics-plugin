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
package io.github.janbarari.gradle.analytics.data.database.config

/**
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
class MySqlDatabaseConfig(block: MySqlDatabaseConfig.() -> Unit): DatabaseConfig() {

    companion object {
        private const val DEFAULT_MYSQL_PORT = 3306
    }

    init {
        also(block)
    }

    /**
     * Host IP.
     */
    lateinit var hostIp: String

    /**
     * Database name
     */
    lateinit var name: String

    /**
     * Connection port, Default port is `3306`.
     */
    var port: Int = DEFAULT_MYSQL_PORT

}
