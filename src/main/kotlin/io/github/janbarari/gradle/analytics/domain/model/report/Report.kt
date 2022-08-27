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
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
data class Report(

    @Json(name = "branch")
    val branch: String,

    @Json(name = "requested_tasks")
    val requestedTasks: String,

    @Json(name = "initialization_process_report")
    var initializationProcessReport: InitializationProcessReport? = null,

    @Json(name = "configuration_process_report")
    var configurationProcessReport: ConfigurationProcessReport? = null,

    @Json(name = "execution_process_report")
    var executionProcessReport: ExecutionProcessReport? = null,

    @Json(name = "overall_build_process_report")
    var overallBuildProcessReport: OverallBuildProcessReport? = null,

    @Json(name = "modules_source_count_report")
    var modulesSourceCountReport: ModulesSourceCountReport? = null,

    @Json(name = "modules_method_count_report")
    var modulesMethodCountReport: ModulesMethodCountReport? = null,

    @Json(name = "cache_hit_report")
    var cacheHitReport: CacheHitReport? = null,

    @Json(name = "success_build_rate_report")
    var successBuildRateReport: SuccessBuildRateReport? = null,

    @Json(name = "dependency_resolve_process_report")
    var dependencyResolveProcessReport: DependencyResolveProcessReport? = null,

    @Json(name = "parallel_execution_rate_report")
    var parallelExecutionRateReport: ParallelExecutionRateReport? = null,

    @Json(name = "modules_execution_process_report")
    var modulesExecutionProcessReport: ModulesExecutionProcessReport? = null,

    @Json(name = "modules_dependency_graph_report")
    var modulesDependencyGraphReport: ModulesDependencyGraphReport? = null

) : java.io.Serializable {

    fun toJson(): String {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Report> = ReportJsonAdapter(moshi)
        return jsonAdapter.toJson(this)
    }

}

