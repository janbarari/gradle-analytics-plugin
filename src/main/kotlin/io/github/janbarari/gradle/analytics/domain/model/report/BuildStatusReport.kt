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
package io.github.janbarari.gradle.analytics.domain.model.report

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.janbarari.gradle.ExcludeJacocoGenerated
import java.io.Serializable

@ExcludeJacocoGenerated
@JsonClass(generateAdapter = true)
data class BuildStatusReport(
    @Json(name = "cumulative_overall_build_process_by_seconds")
    val cumulativeOverallBuildProcessBySeconds: Long,
    @Json(name = "avg_overall_build_process_by_seconds")
    val avgOverallBuildProcessBySeconds: Long,
    @Json(name = "total_build_process_count")
    val totalBuildProcessCount: Int,
    @Json(name = "total_project_modules_count")
    val totalProjectModulesCount: Int,
    @Json(name = "cumulative_parallel_execution_by_seconds")
    val cumulativeParallelExecutionBySeconds: Long,
    @Json(name = "avg_parallel_execution_rate")
    val avgParallelExecutionRate: Float,
    @Json(name = "total_succeed_build_count")
    val totalSucceedBuildCount: Int,
    @Json(name = "total_failed_build_count")
    val totalFailedBuildCount: Int,
    @Json(name = "avg_cache_hit_rate")
    val avgCacheHitRate: Float,
    @Json(name = "cumulative_dependency_resolve_by_seconds")
    val cumulativeDependencyResolveBySeconds: Long,
    @Json(name = "avg_initialization_process_by_millis")
    val avgInitializationProcessByMillis: Long,
    @Json(name = "avg_configuration_process_by_millis")
    val avgConfigurationProcessByMillis: Long,
    @Json(name = "avg_execution_process_by_seconds")
    val avgExecutionProcessBySeconds: Long
): Serializable
