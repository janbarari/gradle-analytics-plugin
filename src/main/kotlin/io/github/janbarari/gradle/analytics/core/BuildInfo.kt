package io.github.janbarari.gradle.analytics.core

data class BuildInfo(
    val tasks: Collection<TaskInfo>,
    val startTime: Long,
    val endTime: Long
)
