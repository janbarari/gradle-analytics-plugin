package io.github.janbarari.gradle.analytics.plugin

import io.github.janbarari.gradle.utils.GitUtils
import io.github.janbarari.gradle.utils.getSafeResourceAsStream
import io.github.janbarari.gradle.utils.isNull
import io.github.janbarari.gradle.utils.openSafeStream
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.Test
import java.io.File
import java.io.IOException

class ReportTest {

    @Test
    fun testBranch() {
        println(GitUtils.getCurrentBranch())
    }

}