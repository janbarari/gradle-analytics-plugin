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
): java.io.Serializable
