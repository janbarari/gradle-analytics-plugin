package io.github.janbarari.gradle.os

import oshi.SystemInfo
import oshi.software.os.OperatingSystem

class OperatingSystemImp : io.github.janbarari.gradle.os.OperatingSystem {

    private val systemInfo: OperatingSystem = SystemInfo().operatingSystem

    override fun getType(): String {
        return systemInfo.family
    }

    override fun getManufacturer(): String {
        return systemInfo.manufacturer
    }

}
