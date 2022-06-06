package io.github.janbarari.gradle.analytics.domain.model

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class Report(
    val branch: String,
    val requestedTasks: String
) : java.io.Serializable {

    var initializationReport: InitializationReport? = null

    var configurationReport: ConfigurationReport? = null

    var executionReport: ExecutionReport? = null

    var totalBuildReport: TotalBuildReport? = null

    fun toJson(): String {
        val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<Report> = moshi.adapter(Report::class.java)
        return jsonAdapter.toJson(this)
    }

}

