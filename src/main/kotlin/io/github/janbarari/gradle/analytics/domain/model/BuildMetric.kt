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
import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
data class BuildMetric(
    @Json(name = "branch")
    var branch: String,
    @Json(name = "requestedTasks")
    var requestedTasks: List<String>,
    @Json(name = "created_at")
    var createdAt: Long,
    @Json(name = "initialization_metric")
    var initializationMetric: InitializationMetric? = null,
    @Json(name = "configuration_metric")
    var configurationMetric: ConfigurationMetric? = null,
    @Json(name = "execution_metric")
    var executionMetric: ExecutionMetric? = null,
    @Json(name = "total_build_metric")
    var totalBuildMetric: TotalBuildMetric? = null
): java.io.Serializable {

    companion object {
        const val INITIALIZATION_SKIP_THRESHOLD = 50L
        const val CONFIGURATION_SKIP_THRESHOLD = 50L
        const val EXECUTION_SKIP_THRESHOLD = 50L
        const val TOTAL_BUILD_SKIP_THRESHOLD = 50L
    }

}
