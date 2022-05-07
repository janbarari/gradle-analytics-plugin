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
 * This table represents how to hold the task information in the SQLite database.
 */
object Task : Table("task") {

    /**
     * The unique auto-generated number which represents the executed task id in the table.
     *
     * It also is the primary-key of the table.
     */
    val id = long("id").autoIncrement().uniqueIndex()

    /**
     * Represents the name of the task.
     */
    val name = varchar("name", VARCHAR_DEFAULT_LENGTH)

    /**
     * Represents the task full path.
     */
    val path = varchar("path", VARCHAR_DEFAULT_LENGTH)

    /**
     * Represents the module of the task.
     */
    val module = varchar("module", VARCHAR_DEFAULT_LENGTH)

    /**
     * The task execution started timestamp.
     */
    val startedAt = long("started_at")

    /**
     * The task execution finished timestamp.
     */
    val finishedAt = long("finished_at")

    /**
     * Every task is a subtree of a build, This is the build number identifier number.
     */
    val buildNumber = long("build_number") references Build.number

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}
