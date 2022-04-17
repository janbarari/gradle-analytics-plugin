package io.github.janbarari.gradle.analytics.core.exception

import io.github.janbarari.gradle.analytics.plugin.PluginConstants.MINIMUM_GRADLE_VERSION
import io.github.janbarari.gradle.analytics.plugin.PluginConstants.PLUGIN_NAME

class PluginCompatibilityException : Exception(
    "$PLUGIN_NAME is compatible with gradle version $MINIMUM_GRADLE_VERSION and above"
)
