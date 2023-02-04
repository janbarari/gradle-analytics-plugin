<!--
 MIT License
 Copyright (c) 2022 Mehdi Janbarari (@janbarari)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
-->

# Setup
To set up the plugin in your project, follow the below instructions:

### <strong>Step 1</strong>
Make sure your project uses Git VCS and Gradle version `6.1+`.
!!! Note ""

    Gradle Analytics Plugin uses Git terminal to get the branch names and latest HEAD commit hash. 
    so It is required for your project to use Git VCS.

<br/><br/>

### <strong>Step 2</strong>
Apply the Gradle Plugin to the root of your project.
=== "Kotlin"
    ``` kotlin
    plugins {
        id("io.github.janbarari.gradle-analytics-plugin") version "1.0.0-beta7"
    }
    ```
=== "Groovy"
    ``` groovy
    plugins {
        id 'io.github.janbarari.gradle-analytics-plugin' version '1.0.0-beta7'
    }
    ```
[For legacy plugin application, see the Gradle Plugin Portal.](https://plugins.gradle.org/plugin/io.github.janbarari.gradle-analytics-plugin)
<br/><br/>

### <strong>Step 3</strong> 
Add plugin configuration in the root of your project.

=== "Kotlin"
    ``` kotlin
    gradleAnalyticsPlugin {
        isEnabled = true // Optional: By default it's True.

        database {
            local = sqlite {
                path = "DATABASE_PATH"
                name = "DATABASE_NAME" // Don't add `.db` in the database name.
                user = "DATABASE_USER" // Remove `user` if you want the plugin to create the DB.
                password = "DATABASE_PASSWORD" // Remove `password` if you want the plugin to create the DB.
            }
            ci = mysql {
                host = "MYSQL_DATABASE_HOST"
                name = "MYSQL_DATABASE_NAME"
                user = "MYSQL_DATABASE_USER"
                password = "MYSQL_DATABASE_PASSWORD"
                port = MYSQL_DATABASE_PORT // Optional: Default is 3306.
            }
        }
    
        trackingTasks = setOf(
            // Add your requested tasks to be analyzed, Example:
            ":app:assembleDebug",
            ":jar", 
            ":assemble"
        )
    
        trackingBranches = setOf(
            // requested tasks only analyzed in the branches you add here, Example:
            "master",
            "develop"
        )

        isTrackAllBranchesEnabled = false // Optional: Default is False.

        outputPath = "OUTPUT_REPORT_PATH" // Optional: Default is project /build/ dir.
    }
    ```
=== "Groovy"
    ``` groovy
    gradleAnalyticsPlugin {
        isEnabled = true // Optional: By default it's True.

        database {
            local = sqlite {
                path = 'DATABASE_PATH'
                name = 'DATABASE_NAME' // Don't add `.db` in the database name.
                user = 'DATABASE_USER' // Remove `user` if you want the plugin to create the DB.
                password = 'DATABASE_PASSWORD' // Remove `password` if you want the plugin to create the DB.
            }
            ci = mysql {
                host = 'MYSQL_DATABASE_HOST'
                name = 'MYSQL_DATABASE_NAME'
                user = 'MYSQL_DATABASE_USER'
                password = 'MYSQL_DATABASE_PASSWORD'
                port = MYSQL_DATABASE_PORT // Optional: Default is 3306.
            }
        }
    
        trackingTasks = [
            // Add your requested tasks to be analyzed, Example:
            ':app:assembleDebug',
            ':jar', 
            ':assemble'
        ]
    
        trackingBranches = [
            // requested tasks only analyzed in the branches you add here, Example:
            'master',
            'develop'
        ]
    
        isTrackAllBranchesEnabled = false // Optional: Default is False.

        outputPath = 'OUTPUT_REPORT_PATH' // Optional: Default is project /build/ dir.
    }
    ```
<br/>
<strong>Important Notes</strong><br/>

- The plugin will create one automatically if there isn't an SQLite database. You only need to fill in the `name` and `path` (Recommended).
- Both `sqlite / mysql` can be used to config `local` or `ci` databases.
- Both `local` and `ci` configs are optional.
- If using the plugin in your CI/CD make sure the `CI=true` environment variable exists in your CI system environments and the `ci` database is configured.
- The `outputPath` can be skipped, it will generate the report inside the project build directory.
- By enabling `isTrackAllBranchesEnabled` analytics will be kicked on all branches.
- `isEnabled` and `isTrackAllBranchesEnabled` are not mandatory since they have default values.

<br/>
