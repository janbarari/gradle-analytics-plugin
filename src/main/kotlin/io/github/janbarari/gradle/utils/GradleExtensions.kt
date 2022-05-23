package io.github.janbarari.gradle.utils

import org.gradle.api.invocation.Gradle

fun Gradle.getRequestedTasks(): List<String> {
    return startParameter.taskNames
}
