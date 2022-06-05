package io.github.janbarari.gradle.analytics.reporttask.exception

class EmptyMetricsException: Throwable() {
    override val message: String = "No metrics are available for generating reports!"
}
