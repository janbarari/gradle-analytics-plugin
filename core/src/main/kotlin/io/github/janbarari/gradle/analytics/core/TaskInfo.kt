package io.github.janbarari.gradle.analytics.core

data class TaskInfo(
    val startTime: Long,
    val endTime: Long,
    val path: String,
    val displayName: String,
    val name: String
)