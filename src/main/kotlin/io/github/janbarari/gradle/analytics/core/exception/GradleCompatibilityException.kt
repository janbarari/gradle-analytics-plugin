package io.github.janbarari.gradle.analytics.core.exception

class GradleCompatibilityException(feature: String ,minimumRequiredVersion: String) : Exception(
    "gradle-analytics-plugin($feature) is compatible with gradle version $minimumRequiredVersion and above"
)
