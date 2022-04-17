package io.github.janbarari.gradle.analytics.core

import io.github.janbarari.gradle.analytics.core.exception.PluginCompatibilityException
import io.github.janbarari.gradle.analytics.plugin.PluginConstants
import org.gradle.util.GradleVersion

/**
 * It makes sure the gradle version is compatible with plugin required gradle version.
 */
@Throws(PluginCompatibilityException::class)
fun ensurePluginCompatibleWithGradle() {
    val currentGradleVersion = GradleVersion.current()
    val minimumRequiredGradleVersion = GradleVersion.version(PluginConstants.MINIMUM_GRADLE_VERSION)
    if (currentGradleVersion < minimumRequiredGradleVersion) {
        throw PluginCompatibilityException()
    }
}
