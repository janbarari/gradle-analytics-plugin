package io.github.janbarari.gradle.analytics.plugin.di

import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

fun KoinComponent.setupKoin() {
    startKoin {
        modules(pluginModule)
    }
}

