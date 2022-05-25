package io.github.janbarari.gradle.utils

import org.junit.jupiter.api.Test

class MathUtilsTest {

    @Test
    fun `test the longMean works fine`() {
        val dataset1 = arrayListOf<Long>(
            100, 200, 300, 210, 30000
        )
        println(MathUtils.longMedian(dataset1))
    }

}