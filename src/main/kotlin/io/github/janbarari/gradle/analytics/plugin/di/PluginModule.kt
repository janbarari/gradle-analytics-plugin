package io.github.janbarari.gradle.analytics.plugin.di

import io.github.janbarari.gradle.os.OperatingSystem
import io.github.janbarari.gradle.os.OperatingSystemImp
import org.koin.dsl.module

val pluginModule = module {

    single {
        OperatingSystemImp() as OperatingSystem
    }

}
