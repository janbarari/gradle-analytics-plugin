/**
 * MIT License
 * Copyright (c) 2024 Mehdi Janbarari (@janbarari)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle

import io.github.janbarari.gradle.extension.whenNotNull
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

class GradleAnalyticsPluginTest {

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

    @Test
    fun `When local postgres database 'host' is missing, Expect thrown exception with detailed message`() {
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
                            local = postgres {
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
                .contains("gradleAnalyticsPlugin: `host` is missing in local Postgres database configuration.")
        }

        testProjectDir.delete()
    }

    @Test
    fun `When local postgres database 'name' is missing, Expect thrown exception with detailed message`() {
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
                            local = postgres {
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
                .contains("gradleAnalyticsPlugin: `name` is missing in local Postgres database configuration.")
        }

        testProjectDir.delete()
    }

    @Test
    fun `When ci postgres database 'host' is missing, Expect thrown exception with detailed message`() {
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
                            ci = postgres {
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
                .contains("gradleAnalyticsPlugin: `host` is missing in ci Postgres database configuration.")
        }

        testProjectDir.delete()
    }

    @Test
    fun `When ci postgres database 'name' is missing, Expect thrown exception with detailed message`() {
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
                            ci = postgres {
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
                .contains("gradleAnalyticsPlugin: `name` is missing in ci Postgres database configuration.")
        }

        testProjectDir.delete()
    }

}
