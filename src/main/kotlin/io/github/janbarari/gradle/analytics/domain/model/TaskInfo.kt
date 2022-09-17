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
package io.github.janbarari.gradle.analytics.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.gradle.tooling.Failure
import org.gradle.tooling.events.OperationDescriptor

@JsonClass(generateAdapter = true)
data class TaskInfo(
    @Json(name = "started_at")
    val startedAt: Long,
    @Json(name = "finished_at")
    val finishedAt: Long,
    @Json(name = "path")
    val path: String,
    @Json(name = "display_name")
    val displayName: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "is_successful")
    val isSuccessful: Boolean,
    @Json(name = "failures")
    val failures: List<Failure>?,
    @Json(name = "dependencies")
    val dependencies: List<OperationDescriptor>?,
    @Json(name = "is_incremental")
    val isIncremental: Boolean,
    @Json(name = "is_from_cache")
    val isFromCache: Boolean,
    @Json(name = "is_up_to_date")
    val isUpToDate: Boolean,
    @Json(name = "is_skipped")
    val isSkipped: Boolean,
    @Json(name = "execution_reasons")
    val executionReasons: List<String>?
) : java.io.Serializable {

    /**
     * Returns the task execution duration in milliseconds.
     */
    fun getDurationInMillis(): Long {
        if (finishedAt < startedAt) return 0L
        return finishedAt - startedAt
    }

    /**
     * Returns the task module name.
     */
    fun getModule(): String {
        val module = path.split(":")
        return if (module.size > 2) module.toList()
            .dropLast(1)
            .joinToString(separator = ":")
        else "no_module"
    }

}
