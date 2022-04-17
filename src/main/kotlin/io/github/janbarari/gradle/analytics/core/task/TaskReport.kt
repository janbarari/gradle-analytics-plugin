package io.github.janbarari.gradle.analytics.core.task

data class TaskReport(
    val startTime: Long,
    val endTime: Long,
    val path: String,
    val displayName: String,
    val name: String
): java.io.Serializable
