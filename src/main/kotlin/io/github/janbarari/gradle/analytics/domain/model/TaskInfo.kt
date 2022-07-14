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

import org.gradle.tooling.Failure
import org.gradle.tooling.events.OperationDescriptor

/**
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
data class TaskInfo(
    val startedAt: Long,
    val finishedAt: Long,
    val path: String,
    val displayName: String,
    val name: String,
    val isSuccessful: Boolean,
    val failures: List<Failure>?,
    val dependencies: List<OperationDescriptor>?,
    val isIncremental: Boolean,
    val isFromCache: Boolean,
    val isUpToDate: Boolean,
    val executionReasons: List<String>?
) : java.io.Serializable {

    /**
     * Returns the task execution duration in milliseconds.
     */
    fun getDuration(): Long {
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
