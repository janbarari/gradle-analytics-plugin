import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.gitlab.arturbosch.detekt").version("1.20.0-RC2")
    kotlin("jvm") version "1.6.10"
    jacoco
    id("java-gradle-plugin")
}

repositories {
    mavenCentral()
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
    compileOnly(gradleApi())
    implementation(project(":core"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
    }
}

gradlePlugin {
    plugins {
        create("io.github.janbarari.gradle-analytics-plugin") {
            id = "io.github.janbarari.gradle-analytics-plugin"
            implementationClass = "io.github.janbarari.gradle.analytics.plugin.GradleAnalyticsPlugin"
        }
    }
}