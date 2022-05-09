package io.github.janbarari.gradle.logger

import io.github.janbarari.gradle.logger.Logger
import io.github.janbarari.gradle.logger.LoggerImp
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoggerTest {

    private lateinit var logger: Logger

    @BeforeAll
    fun setup() {
        logger = LoggerImp()
    }

    @Test
    fun `log should not be visible on SILENT mode`() {
        logger.setMode(Logger.LogMode.SILENT)
        val result = logger.log("Test", "Hello World!")
        assertEquals(false, result)
    }

    @Test
    fun `log should be visible on INFO mode`() {
        logger.setMode(Logger.LogMode.INFO)
        val result = logger.log("Test", "Hello World!")
        assertEquals(true, result)
    }

    @Test
    fun `error should be work on any mode`() {
        logger.setMode(Logger.LogMode.INFO)
        val infoModeResult = logger.error("INFO")
        assertEquals(true, infoModeResult)
        logger.setMode(Logger.LogMode.SILENT)
        val silentModeResult = logger.error("SILENT")
        assertEquals(true, silentModeResult)
    }

    @Test
    fun `mode should change to INFO`() {
        logger.setMode(Logger.LogMode.INFO)
        assertEquals(logger.getMode(), Logger.LogMode.INFO)
    }

}
