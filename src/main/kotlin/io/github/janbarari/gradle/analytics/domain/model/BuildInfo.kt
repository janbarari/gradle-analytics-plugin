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

import java.time.Duration

data class BuildInfo(
    val createdAt: Long,
    val startedAt: Long,
    val initializedAt: Long,
    val configuredAt: Long,
    val dependenciesResolveInfo: Collection<DependencyResolveInfo>,
    val executedTasks: List<TaskInfo>,
    val finishedAt: Long,
    val osInfo: OsInfo,
    val hardwareInfo: HardwareInfo,
    val branch: String,
    val requestedTasks: List<String>
) : java.io.Serializable {

    /**
     * Returns the total build duration.
     */
    fun getTotalDuration(): Duration {
        if (finishedAt < startedAt) return Duration.ofMillis(0)
        return Duration.ofMillis(finishedAt - startedAt)
    }

    /**
     * Returns the build initialization process duration.
     */
    fun getInitializationDuration(): Duration {
        if (initializedAt < startedAt) return Duration.ofMillis(0)
        return Duration.ofMillis(initializedAt - startedAt)
    }

    /**
     * Returns the build configuration process duration.
     */
    fun getConfigurationDuration(): Duration {
        if (configuredAt < startedAt) return Duration.ofMillis(0)
        return Duration.ofMillis(configuredAt - startedAt)
    }

    /**
     * Returns the build execution process duration.
     */
    fun getExecutionDuration(): Duration {
        if (finishedAt < configuredAt) return Duration.ofMillis(0)
        return Duration.ofMillis(finishedAt - configuredAt)
    }

    /**
     * Returns the total dependencies resolve duration.
     */
    fun getTotalDependenciesResolveDuration(): Duration {
        var result = 0L
        val iterator = dependenciesResolveInfo.iterator()
        while (iterator.hasNext()) {
            val info = iterator.next()
            result += info.getDuration()
        }
        return Duration.ofMillis(result)
    }

}
