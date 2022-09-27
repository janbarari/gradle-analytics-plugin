package io.github.janbarari.gradle.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TerminalUtilsTest {

    @Test
    fun `check execCommand() throws exception`() {
        assertThrows<java.lang.RuntimeException> {
            TerminalUtils.execCommand("git alaki")
        }
    }

}