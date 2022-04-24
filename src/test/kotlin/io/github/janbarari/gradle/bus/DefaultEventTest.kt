package io.github.janbarari.gradle.bus

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultEventTest {

    private lateinit var event: DefaultEvent

    @BeforeAll
    fun setup() {
        event = DefaultEvent(
            DefaultEventTest::class.java
        )
        event.put("isTest", true)
    }

    @Test
    fun `test getters`() {
        event.toString()

        assertEquals(
            event.getSender(), DefaultEventTest::class.java
        )

        assertTrue(
            event.containsKey("isTest")
        )

        assertEquals(
            true, event.get("isTest") as Boolean
        )
    }

    @Test
    fun `test toString is working correctly`() {
        println(event.toString())

        assertTrue(
            event.toString().contains(DefaultEventTest::class.java.toString())
        )

        assertTrue(
            event.toString().contains("isTest=true")
        )

    }

}