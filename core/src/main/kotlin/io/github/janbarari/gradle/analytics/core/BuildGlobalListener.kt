package io.github.janbarari.gradle.analytics.core

object BuildGlobalListener {

    var listener: ((buildInfo: BuildInfo) -> Unit)? = null

    fun buildFinished(buildInfo: BuildInfo) {
        listener?.invoke(buildInfo)
    }

}