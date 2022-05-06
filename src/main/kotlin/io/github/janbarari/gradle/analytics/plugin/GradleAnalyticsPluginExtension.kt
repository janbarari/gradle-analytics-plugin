package io.github.janbarari.gradle.analytics.plugin

import io.github.janbarari.gradle.analytics.data.database.DatabaseConfig
import org.gradle.api.Project

open class GradleAnalyticsPluginExtension(val project: Project) {

    private var databaseConfig: DatabaseConfig = DatabaseConfig()

    fun databaseConfig(block: DatabaseConfig.() -> Unit) {
        databaseConfig = DatabaseConfig().also(block)
    }

    fun getDatabaseConfig(): DatabaseConfig = databaseConfig

}
