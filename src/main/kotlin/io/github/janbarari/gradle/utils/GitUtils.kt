package io.github.janbarari.gradle.utils

object GitUtils {

    fun getCurrentBranch(): String {
        return execCommand("git rev-parse --abbrev-ref HEAD")
    }

}
