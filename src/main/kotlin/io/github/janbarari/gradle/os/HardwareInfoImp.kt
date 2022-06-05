package io.github.janbarari.gradle.os

import oshi.SystemInfo

class HardwareInfoImp: HardwareInfo {

    private val systemInfo = SystemInfo()

    override fun availableMemory(): Long {
        return systemInfo.hardware.memory.available
    }

    override fun totalMemory(): Long {
        return systemInfo.hardware.memory.total
    }

}
