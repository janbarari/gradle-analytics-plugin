# Setup
To set up the plugin in your project, follow the below instructions:

### <strong>Step 1</strong>
Make sure your project uses Git VCS.
!!! Note ""

    Gradle Analytics Plugin uses Git terminal to get the branch names and latest HEAD commit hash. 
    so It is required for your project to use Git VCS.

<br/><br/>

### <strong>Step 2</strong>
Apply the Gradle Plugin to the root of your project.
=== "Kotlin"
    ``` kotlin
    plugins {
        id("io.github.janbarari.gradle-analytics-plugin") version "1.0.0-beta1"
    }
    ```
=== "Groovy"
    ``` groovy
    plugins {
        id "io.github.janbarari.gradle-analytics-plugin" version "1.0.0-beta1"
    }
    ```
[For legacy plugin application, see the Gradle Plugin Portal.](https://plugins.gradle.org/plugin/io.github.janbarari.gradle-analytics-plugin)
<br/><br/>

### <strong>Step 3</strong> 
Add plugin configuration in the root of your project.

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
                port = MYSQL_DATABASE_PORT // Default is 3306
            }
        }
    
        trackingTasks = listOf(
            // Add your requested tasks to be analyzed, Example:
            ":app:assembleDebug"
        )
    
        trackingBranches = listOf(
            // requested tasks only analyzed in the branches you add here, Example:
            "master",
            "develop"
        )
    
        outputPath = "OUTPUT_REPORT_PATH"
    }
    ```
=== "Groovy"
    ``` groovy
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
                port = MYSQL_DATABASE_PORT // Default is 3306
            }
        }
    
        trackingTasks = [
            // Add your requested tasks to be analyzed, Example:
            ":app:assembleDebug"
        ]
    
        trackingBranches = [
            // requested tasks only analyzed in the branches you add here, Example:
            "master",
            "develop"
        ]
    
        outputPath = "OUTPUT_REPORT_PATH"
    }
    ```
<br/>
<strong>Important Notes</strong><br/>

- If you don't have a sqlite database, the plugin will create one automatically. You only need to the `name` and `path` for it.
- You can choose both `sqlite / mysql` for `local` or `ci`.
- You can skip `local` or `ci` database if you don't need analytics on each of them.
- If you use `ci` make sure the `CI=true` environment variable exists in your CI system environments.

<br/>
