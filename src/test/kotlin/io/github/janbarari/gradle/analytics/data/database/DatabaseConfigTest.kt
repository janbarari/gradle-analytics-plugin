package io.github.janbarari.gradle.analytics.data.database

import io.github.janbarari.gradle.analytics.data.database.exception.DatabaseConfigNotDefinedException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class DatabaseConfigTest {

    @Test
    fun `check ensureRequiredInputsExists() function throws exception when url is not initialized`() {
        val dc = DatabaseConfig()
        dc.isQueryLogEnabled = true
        dc.password = "123"
        dc.user = "janbarari"
        assertThrows<DatabaseConfigNotDefinedException> {
            dc.ensureRequiredInputsExist()
        }
    }

    @Test
    fun `check ensureRequiredInputsExists() function throws exception when user is not initialized`() {
        val dc = DatabaseConfig()
        dc.url = "/build/temporary.db"
        dc.isQueryLogEnabled = true
        dc.password = "123"
        assertThrows<DatabaseConfigNotDefinedException> {
            dc.ensureRequiredInputsExist()
        }
    }

    @Test
    fun `check ensureRequiredInputsExists() function returns true when everything initialized`() {
        val dc = DatabaseConfig()
        dc.url = "/build/temporary.db"
        dc.user = "janbarari"
        dc.isQueryLogEnabled = true
        dc.password = "123"
        assert(dc.ensureRequiredInputsExist())
    }

    @Test
    fun `check the getters and setters`() {
        val dc = DatabaseConfig()

        assertThrows<UninitializedPropertyAccessException> {
            dc.url
        }

        assertThrows<UninitializedPropertyAccessException> {
            dc.user
        }

        dc.url = "/build/temporary.db"
        assertEquals("/build/temporary.db", dc.url)

        dc.user = "janbarari"
        assertEquals("janbarari", dc.user)

        dc.password = "123"
        assertEquals("123", dc.password)

        dc.isQueryLogEnabled = true
        assertEquals(true, dc.isQueryLogEnabled)
    }

}