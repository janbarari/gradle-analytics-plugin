package io.github.janbarari.gradle.os

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OperatingSystemTest {

    private lateinit var os: OperatingSystem

    @BeforeAll
    fun setup() {
        os = OperatingSystemImp()
    }

    @Test
    fun `Check the getName() returns the OS name`() {
        val osName = os.getName()
        assert(osName.isNotEmpty())
    }

    @Test
    fun `Check the getManufacturer() returns the OS manufacturer name`() {
        val osManufacturer = os.getManufacturer()
        assert(osManufacturer.isNotEmpty())
    }

}