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
package io.github.janbarari.gradle.analytics.domain.entity

import io.github.janbarari.gradle.analytics.domain.VARCHAR_DEFAULT_LENGTH
import org.jetbrains.exposed.sql.Table

/**
 * This table represents how to hold the various build in the SQLite database.
 */
object Build : Table("build") {

    /**
     * The unique auto-generated number which represents the build-number.
     *
     * It also is the primary-key of the table.
     */
    val number = long("number").autoIncrement().uniqueIndex()

    /**
     * The build started timestamp.
     */
    val startedAt = long("started_at")

    /**
     * The build finished timestamp
     */
    val finishedAt = long("finished_at")

    /**
     * The configuration finished timestamp.
     */
    val configurationFinishedAt = long("configuration_finished_at")

    /**
     * The execution terminal/command-prompt command.
     */
    val cmd = varchar("cmd", VARCHAR_DEFAULT_LENGTH).nullable()

    /**
     * The executor operating system name.
     */
    val os = varchar("os", VARCHAR_DEFAULT_LENGTH).nullable()

    override val primaryKey = PrimaryKey(number)
}
