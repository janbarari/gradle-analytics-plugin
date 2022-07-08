package io.github.janbarari.gradle.extension

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IntExtensionsTest {

    @Test
    fun `check diffPercentageOf() returns positive percentage`() {
        val first = 400
        val last = 589
        assertEquals(47.25f, first.diffPercentageOf(last))
    }

    @Test
    fun `check toPercentageOf() returns correct result`() {
        val first = 5
        val target = 62
        println(first.toPercentageOf(target))
        assertEquals(8.06f, first.toPercentageOf(target))
    }

}
