package io.github.janbarari.gradle.os

import io.github.janbarari.gradle.ExcludeJacocoGenerated

@ExcludeJacocoGenerated
fun provideHardwareInfo(): HardwareInfo {
    return HardwareInfoImp()
}

@ExcludeJacocoGenerated
fun provideOperatingSystem(): OperatingSystem {
    return OperatingSystemImp()
}
