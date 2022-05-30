package io.github.janbarari.gradle.extension

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StringExtensionsTest {

    @Test
    fun `check toFilePath() returns correct path`() {
        val path1 = "/Users/spider/project/namoos/"
        assertEquals("/Users/spider/project/namoos", path1.toRealPath())
        val path2 = "/Users/spider/project/namoos//"
        assertEquals("/Users/spider/project/namoos", path2.toRealPath())
        val path3 = "/Users/spider/project/namoos///"
        assertEquals("/Users/spider/project/namoos", path3.toRealPath())
    }

}