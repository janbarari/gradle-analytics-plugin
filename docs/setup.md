# Setup
To setup the plugin in your projects, follow the below instructions:

<strong>Step 1:<br /></strong> Apply the Gradle Plugin to the root of your project.
=== "Kotlin"
    ``` kotlin
    plugins {
        id("io.github.janbarari.gradle-analytics-plugin") version "1.0.0-beta04"
    }
    ```
=== "Groovy"
    ``` groovy
    plugins {
        id "io.github.janbarari.gradle-analytics-plugin" version "1.0.0-beta04"
    }
    ```
[For legacy plugin application, see the Gradle Plugin Portal.](https://plugins.gradle.org/plugin/io.github.janbarari.gradle-analytics-plugin)

<br/><br/>
<strong>Step 2:<br /></strong> Add plugin configuration in the root of your project.

=== "Kotlin"
    ``` kotlin
    gradleAnalyticsPlugin {
        database {
            local = sqlite {
                path = "DATABASE_PATH"
                name = "DATABASE_NAME"
                user = "DATABASE_USER"
                password = "DATABASE_PASSWORD"
            }
            ci = mysql {
                host = "MYSQL_DATABASE_HOST",
                name = "MYSQL_DATABASE_NAME",
                user = "MYSQL_DATABASE_USER",
                password = "MYSQL_DATABASE_PASSWORD"
            }
        }
    
        trackingTasks = listOf(
            ":app:assembleDebug",
            "clean test build"
        )
    
        trackingBranches = listOf(
            "master",
            "develop"
        )
    
        outputPath = "/path/to/save/report"
    }
    ```
=== "Groovy"
    ``` groovy
    gradleAnalyticsPlugin {
        database {
            local = sqlite {
                path = "/Users/workstation/desktop"
                name = "temporary-myapplication-db"
            }
            
            ci = mysql {
                host = ""
                user = ""
                password = ""
                name = ""
            }
        }
    
        trackingTasks = [
                ":app:assembleDebug",
                ":app:assembleRelease"
        ]
    
        trackingBranches = [
                "main"
        ]
    
        outputPath = "/Users/workstation/Desktop"
    }
    ```
