package io.github.janbarari.gradle.analytics.domain.model

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.janbarari.gradle.extension.ExcludeJacocoGenerated

data class AnalyticsReport(
    val branch: String,
    val requestedTasks: String
): java.io.Serializable {

    var initializationReport: InitializationReport? = null

    var configurationReport: ConfigurationReport? = null

    fun toJson(): String {
        val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<AnalyticsReport> = moshi.adapter(AnalyticsReport::class.java)
        return jsonAdapter.toJson(this)
    }

}

