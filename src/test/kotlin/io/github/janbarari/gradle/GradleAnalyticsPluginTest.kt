package io.github.janbarari.gradle

import io.github.janbarari.gradle.extension.whenNotNull
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.gradle.testkit.runner.TaskOutcome

class GradleAnalyticsPluginTest {

    @Test
    fun `Ensure the plugin is successfully installed on the sample project`() {
        val testProjectDir = TemporaryFolder()
        testProjectDir.create()
        val buildFile = testProjectDir.newFile("build.gradle")
        buildFile.appendText(
            """
                   plugins {
                      id 'java'
                      id 'io.github.janbarari.gradle-analytics-plugin'
                   }
                   
                   gradleAnalyticsPlugin {
                        database {
                            local = sqlite {
                                path = '${testProjectDir.root}'
                                name = 'test-database'
                            }
                        }
                            
                        trackingBranches = ['main', 'develop']
                        
                        trackingTasks = ['assemble']
                        
                        outputPath = '${testProjectDir.root}'
                   }
                    """
        )

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("assemble", "--stacktrace")
            .withPluginClasspath()
            .build()

        result.whenNotNull {
            println("\n==== BEGIN TEST OUTPUT ====")
            println(output)
            println("==== END TEST OUTPUT ====\n")
        }

        assertEquals(TaskOutcome.SUCCESS, result.task(":assemble")?.outcome)

        testProjectDir.delete()
    }

    @Test
    fun `When 'reportAnalytics' task configured in trackingTasks, Expect thrown exception with detailed message`() {
        val testProjectDir = TemporaryFolder()
        testProjectDir.create()
        val buildFile = testProjectDir.newFile("build.gradle")
        buildFile.appendText(
            """
                   plugins {
                      id 'java'
                      id 'io.github.janbarari.gradle-analytics-plugin'
                   }
                   
                   gradleAnalyticsPlugin {
                        database {
                            local = sqlite {
                                path = '${testProjectDir.root}'
                                name = 'test-database'
                            }
                        }
                            
                        trackingBranches = ['main', 'develop']
                        
                        trackingTasks = ['reportAnalytics', 'assemble']
                        
                        outputPath = '${testProjectDir.root}'
                   }
                    """
        )

        var result: BuildResult? = null
        val exception = assertThrows<Throwable> {
            result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("assemble")
                .withPluginClasspath()
                .build()

        }
        println(exception.message)

        result.whenNotNull {
            println("\n==== BEGIN TEST OUTPUT ====")
            println(output)
            println("==== END TEST OUTPUT ====\n")
        }

        assertTrue {
            exception.message!!.contains("gradleAnalyticsPlugin: `reportAnalytics` task is forbidden from being tracked.")
        }

        testProjectDir.delete()
    }

    @Test
    fun `When local sqlite database 'path' is missing, Expect thrown exception with detailed message`() {
        val testProjectDir = TemporaryFolder()
        testProjectDir.create()
        val buildFile = testProjectDir.newFile("build.gradle")
        buildFile.appendText(
            """
                   plugins {
                      id 'java'
                      id 'io.github.janbarari.gradle-analytics-plugin'
                   }
                   
                   gradleAnalyticsPlugin {
                        database {
                            local = sqlite {
                                name = 'test-database'
                            }
                        }
                            
                        trackingBranches = ['main', 'develop']
                        
                        trackingTasks = ['assemble']
                        
                        outputPath = '${testProjectDir.root}'
                   }
                    """
        )

        var result: BuildResult? = null
        val exception = assertThrows<Throwable> {
            result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("assemble")
                .withPluginClasspath()
                .build()

        }
        println(exception.message)

        result.whenNotNull {
            println("\n==== BEGIN TEST OUTPUT ====")
            println(output)
            println("==== END TEST OUTPUT ====\n")
        }

        assertTrue {
            exception
                .message!!
                .contains("gradleAnalyticsPlugin: `path` is missing in local Sqlite database configuration.")
        }

        testProjectDir.delete()
    }

    @Test
    fun `When local sqlite database 'name' is missing, Expect thrown exception with detailed message`() {
        val testProjectDir = TemporaryFolder()
        testProjectDir.create()
        val buildFile = testProjectDir.newFile("build.gradle")
        buildFile.appendText(
            """
                   plugins {
                      id 'java'
                      id 'io.github.janbarari.gradle-analytics-plugin'
                   }
                   
                   gradleAnalyticsPlugin {
                        database {
                            local = sqlite {
                                path = '${testProjectDir.root}'
                            }
                        }
                            
                        trackingBranches = ['main', 'develop']
                        
                        trackingTasks = ['assemble']
                        
                        outputPath = '${testProjectDir.root}'
                   }
                    """
        )

        var result: BuildResult? = null
        val exception = assertThrows<Throwable> {
            result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("assemble")
                .withPluginClasspath()
                .build()

        }
        println(exception.message)

        result.whenNotNull {
            println("\n==== BEGIN TEST OUTPUT ====")
            println(output)
            println("==== END TEST OUTPUT ====\n")
        }

        assertTrue {
            exception
                .message!!
                .contains("gradleAnalyticsPlugin: `name` is missing in local Sqlite database configuration.")
        }

        testProjectDir.delete()
    }

    @Test
    fun `When ci sqlite database 'path' is missing, Expect thrown exception with detailed message`() {
        val testProjectDir = TemporaryFolder()
        testProjectDir.create()
        val buildFile = testProjectDir.newFile("build.gradle")
        buildFile.appendText(
            """
                   plugins {
                      id 'java'
                      id 'io.github.janbarari.gradle-analytics-plugin'
                   }
                   
                   gradleAnalyticsPlugin {
                        database {
                            ci = sqlite {
                                name = 'test-database'
                            }
                        }
                            
                        trackingBranches = ['main', 'develop']
                        
                        trackingTasks = ['assemble']
                        
                        outputPath = '${testProjectDir.root}'
                   }
                    """
        )

        var result: BuildResult? = null
        val exception = assertThrows<Throwable> {
            result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("assemble")
                .withPluginClasspath()
                .build()

        }
        println(exception.message)

        result.whenNotNull {
            println("\n==== BEGIN TEST OUTPUT ====")
            println(output)
            println("==== END TEST OUTPUT ====\n")
        }

        assertTrue {
            exception
                .message!!
                .contains("gradleAnalyticsPlugin: `path` is missing in ci Sqlite database configuration.")
        }

        testProjectDir.delete()
    }

    @Test
    fun `When ci sqlite database 'name' is missing, Expect thrown exception with detailed message`() {
        val testProjectDir = TemporaryFolder()
        testProjectDir.create()
        val buildFile = testProjectDir.newFile("build.gradle")
        buildFile.appendText(
            """
                   plugins {
                      id 'java'
                      id 'io.github.janbarari.gradle-analytics-plugin'
                   }
                   
                   gradleAnalyticsPlugin {
                        database {
                            ci = sqlite {
                                path = '${testProjectDir.root}'
                            }
                        }
                            
                        trackingBranches = ['main', 'develop']
                        
                        trackingTasks = ['assemble']
                        
                        outputPath = '${testProjectDir.root}'
                   }
                    """
        )

        var result: BuildResult? = null
        val exception = assertThrows<Throwable> {
            result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("assemble")
                .withPluginClasspath()
                .build()

        }
        println(exception.message)

        result.whenNotNull {
            println("\n==== BEGIN TEST OUTPUT ====")
            println(output)
            println("==== END TEST OUTPUT ====\n")
        }

        assertTrue {
            exception
                .message!!
                .contains("gradleAnalyticsPlugin: `name` is missing in ci Sqlite database configuration.")
        }

        testProjectDir.delete()
    }

    @Test
    fun `When local mysql database 'host' is missing, Expect thrown exception with detailed message`() {
        val testProjectDir = TemporaryFolder()
        testProjectDir.create()
        val buildFile = testProjectDir.newFile("build.gradle")
        buildFile.appendText(
            """
                   plugins {
                      id 'java'
                      id 'io.github.janbarari.gradle-analytics-plugin'
                   }
                   
                   gradleAnalyticsPlugin {
                        database {
                            local = mysql {
                                name = 'test-database'
                            }
                        }
                            
                        trackingBranches = ['main', 'develop']
                        
                        trackingTasks = ['assemble']
                        
                        outputPath = '${testProjectDir.root}'
                   }
                    """
        )

        var result: BuildResult? = null
        val exception = assertThrows<Throwable> {
            result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("assemble")
                .withPluginClasspath()
                .build()

        }
        println(exception.message)

        result.whenNotNull {
            println("\n==== BEGIN TEST OUTPUT ====")
            println(output)
            println("==== END TEST OUTPUT ====\n")
        }

        assertTrue {
            exception
                .message!!
                .contains("gradleAnalyticsPlugin: `host` is missing in local MySql database configuration.")
        }

        testProjectDir.delete()
    }

    @Test
    fun `When local mysql database 'name' is missing, Expect thrown exception with detailed message`() {
        val testProjectDir = TemporaryFolder()
        testProjectDir.create()
        val buildFile = testProjectDir.newFile("build.gradle")
        buildFile.appendText(
            """
                   plugins {
                      id 'java'
                      id 'io.github.janbarari.gradle-analytics-plugin'
                   }
                   
                   gradleAnalyticsPlugin {
                        database {
                            local = mysql {
                                host = '127.0.0.1'
                            }
                        }
                            
                        trackingBranches = ['main', 'develop']
                        
                        trackingTasks = ['assemble']
                        
                        outputPath = '${testProjectDir.root}'
                   }
                    """
        )

        var result: BuildResult? = null
        val exception = assertThrows<Throwable> {
            result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("assemble")
                .withPluginClasspath()
                .build()

        }
        println(exception.message)

        result.whenNotNull {
            println("\n==== BEGIN TEST OUTPUT ====")
            println(output)
            println("==== END TEST OUTPUT ====\n")
        }

        assertTrue {
            exception
                .message!!
                .contains("gradleAnalyticsPlugin: `name` is missing in local MySql database configuration.")
        }

        testProjectDir.delete()
    }

    @Test
    fun `When ci mysql database 'host' is missing, Expect thrown exception with detailed message`() {
        val testProjectDir = TemporaryFolder()
        testProjectDir.create()
        val buildFile = testProjectDir.newFile("build.gradle")
        buildFile.appendText(
            """
                   plugins {
                      id 'java'
                      id 'io.github.janbarari.gradle-analytics-plugin'
                   }
                   
                   gradleAnalyticsPlugin {
                        database {
                            ci = mysql {
                                name = 'test-database'
                            }
                        }
                            
                        trackingBranches = ['main', 'develop']
                        
                        trackingTasks = ['assemble']
                        
                        outputPath = '${testProjectDir.root}'
                   }
                    """
        )

        var result: BuildResult? = null
        val exception = assertThrows<Throwable> {
            result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("assemble")
                .withPluginClasspath()
                .build()

        }
        println(exception.message)

        result.whenNotNull {
            println("\n==== BEGIN TEST OUTPUT ====")
            println(output)
            println("==== END TEST OUTPUT ====\n")
        }

        assertTrue {
            exception
                .message!!
                .contains("gradleAnalyticsPlugin: `host` is missing in ci MySql database configuration.")
        }

        testProjectDir.delete()
    }

    @Test
    fun `When ci mysql database 'name' is missing, Expect thrown exception with detailed message`() {
        val testProjectDir = TemporaryFolder()
        testProjectDir.create()
        val buildFile = testProjectDir.newFile("build.gradle")
        buildFile.appendText(
            """
                   plugins {
                      id 'java'
                      id 'io.github.janbarari.gradle-analytics-plugin'
                   }
                   
                   gradleAnalyticsPlugin {
                        database {
                            ci = mysql {
                                host = '127.0.0.1'
                            }
                        }
                            
                        trackingBranches = ['main', 'develop']
                        
                        trackingTasks = ['assemble']
                        
                        outputPath = '${testProjectDir.root}'
                   }
                    """
        )

        var result: BuildResult? = null
        val exception = assertThrows<Throwable> {
            result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("assemble")
                .withPluginClasspath()
                .build()
        }
        println(exception.message)

        result.whenNotNull {
            println("\n==== BEGIN TEST OUTPUT ====")
            println(output)
            println("==== END TEST OUTPUT ====\n")
        }

        assertTrue {
            exception
                .message!!
                .contains("gradleAnalyticsPlugin: `name` is missing in ci MySql database configuration.")
        }

        testProjectDir.delete()
    }

}
