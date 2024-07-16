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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

val pluginId: String by project
val pluginDisplayName: String by project
val pluginDescription: String by project
val pluginImplementationClass: String by project
val pluginDeclarationName: String by project
val pluginGroupPackageName: String by project
val pluginVersion: String by project
val pluginWebsite: String by project
val pluginVcsUrl: String by project
val pluginTags: String by project

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.detekt)
    `java-gradle-plugin`
    `maven-publish`
    jacoco
    id("com.google.devtools.ksp") version libs.versions.ksp
    id("com.gradle.plugin-publish") version libs.versions.publish.plugin
}

group = pluginGroupPackageName
version = pluginVersion

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)

    compileOnly(gradleApi())

    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.sqlite.driver)
    implementation(libs.mysql.driver)
    implementation(libs.postgres.driver)
    implementation(libs.jetbrains.exposed.core)
    implementation(libs.jetbrains.exposed.jdbc)
    implementation(libs.moshi)
    ksp(libs.moshi.codegen)
    implementation(libs.commons.io)
    implementation(libs.coroutines)

}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

plugins.withType<JavaPlugin>().configureEach {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {

    val kotlinTree = fileTree(baseDir = "${project.layout.buildDirectory.asFile.get().path}/classes") {
        excludes.add("**/*JsonAdapter.*")
        excludes.add("**/*Test*.*")
    }

    classDirectories.setFrom(kotlinTree)
    executionData.setFrom(files("${project.layout.buildDirectory.asFile.get().path}/jacoco/test.exec"))

    val files = files("src/main/kotlin")

    sourceDirectories.setFrom(files)
    additionalSourceDirs.setFrom(files)

    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
    }
}

publishing {
    publications {
        repositories {
            mavenLocal()
        }
    }
}

pluginBundle {
    website = pluginWebsite
    vcsUrl = pluginVcsUrl
    tags = "$pluginTags".split(",")
}

gradlePlugin {
    plugins {
        create(pluginDeclarationName) {
            id = pluginId
            displayName = pluginDisplayName
            description = pluginDescription
            implementationClass = pluginImplementationClass
        }
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}

tasks.register("publishToLocal") {
    doLast {
        exec {
            commandLine(
                "./gradlew",
                "clean",
                "validateSourceHeaderLicense",
                "detekt",
                "build",
                "publishToMavenLocal",
                "test"
            ).args("--info")
        }
    }
}

tasks.register("unsafePublishToLocal") {
    doLast {
        exec {
            commandLine(
                "./gradlew",
                "build",
                "publishToMavenLocal",
            )
        }
    }
}

tasks.register("validateSourceHeaderLicense") {
    outputs.cacheIf { false }
    doLast {
        val stdout = ByteArrayOutputStream()
        exec {
            executable("./scripts/sourceLicenseValidator.sh")
            standardOutput = stdout
        }
        println(stdout.toString())
    }
}

tasks.register("publishToGradlePortal") {
    doLast {
        val key = System.getenv("GRADLE_PORTAL_KEY")
        val secret = System.getenv("GRADLE_PORTAL_SECRET")
        exec {
            commandLine(
                "./gradlew",
                "publishPlugins",
                "-Pgradle.publish.key=$key",
                "-Pgradle.publish.secret=$secret",
            ).args("--info")
        }
    }
}

detekt {
    config.setFrom(files("detekt-config.yml"))
    buildUponDefaultConfig = true
    source.setFrom(files("src/main/kotlin"))
}
