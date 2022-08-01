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

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
data class Report(
    val branch: String,
    val requestedTasks: String
) : java.io.Serializable {

    var initializationReport: InitializationReport? = null

    var configurationReport: ConfigurationReport? = null

    var executionReport: ExecutionReport? = null

    var overallBuildProcessReport: OverallBuildProcessReport? = null

    var modulesSourceCountReport: ModulesSourceCountReport? = null

    var modulesMethodCountReport: ModulesMethodCountReport? = null

    var cacheHitReport: CacheHitReport? = null

    var buildSuccessRatioReport: BuildSuccessRatioReport? = null

    var dependencyResolveReport: DependencyResolveReport? = null

    var parallelExecutionRateReport: ParallelExecutionRateReport? = null

    fun toJson(): String {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Report> = ReportJsonAdapter(moshi)
        return jsonAdapter.toJson(this)
    }

}

