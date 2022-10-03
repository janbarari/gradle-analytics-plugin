package io.github.janbarari.gradle.extension

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ListExtensionsTest {

    @Test
    fun `when the 'modify' operator invoke, expect the changes in final result`() {
        data class TestModel(val name: String, var age: Int)
        val data = listOf(
            TestModel("Mehdi", 26),
            TestModel("Shahram", 27),
            TestModel("Shabnam", 30)
        )
        data
            .filter { it.name == "Shabnam" }
            .modify {
                age = 31
            }
        assertEquals(data[2].age, 31)
    }

    @Test
    fun `when 'firstIndex' invoked, expect to get the first item index`() {
        val data = listOf(
            "Mehdi",
            "Shahram",
            "Shabnam"
        )
        assertEquals(data[data.firstIndex], "Mehdi")
    }

}
