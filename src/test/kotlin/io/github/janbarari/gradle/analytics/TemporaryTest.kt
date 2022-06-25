package io.github.janbarari.gradle.analytics

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.pathString

class TemporaryTest {

    @Test
    fun getListOfKotlinFiles() {

        val rootDirectory = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker"

        var pathList: List<Path> = arrayListOf()
        Files.walk(Paths.get(rootDirectory)).use { stream ->
            pathList = stream.map { obj: Path -> obj.normalize() }
                .filter(Files::isRegularFile)
                .collect(Collectors.toList())
        }

        pathList
            .filter {
                (it.pathString.contains("src/main/java") || it.pathString.contains("src/main/kotlin"))
                        && it.extension == "kt"
            }
            .forEach {
            println(it)
        }

    }

}