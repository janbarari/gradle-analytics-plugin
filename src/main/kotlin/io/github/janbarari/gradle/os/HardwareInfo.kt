package io.github.janbarari.gradle.os

interface HardwareInfo {

    fun availableMemory(): Long

    fun totalMemory(): Long

}
