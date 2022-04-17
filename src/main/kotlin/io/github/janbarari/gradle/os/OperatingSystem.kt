package io.github.janbarari.gradle.os

interface OperatingSystem {

    /**
     * Returns device operating system type name
     */
    fun getType(): String

    /**
     * Returns device operating system manufacturer name
     */
    fun getManufacturer(): String

}