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
 * This table represents how to hold the build tasks execution information in the SQLite database.
 */
object Task : Table("task") {

    val id = long("id").autoIncrement().uniqueIndex()

    val name = varchar("name", VARCHAR_DEFAULT_LENGTH)

    val path = varchar("path", VARCHAR_DEFAULT_LENGTH)

    val module = varchar("module", VARCHAR_DEFAULT_LENGTH)

    val startedAt = long("started_at")

    val finishedAt = long("finished_at")

    val buildNumber = long("build_number") references Build.number

    override val primaryKey: PrimaryKey = PrimaryKey(id)

}
