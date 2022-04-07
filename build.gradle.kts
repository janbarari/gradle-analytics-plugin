import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val pluginId: String by project
val pluginDisplayName: String by project
val pluginDescription: String by project
val pluginImplementationClass: String by project
val pluginDeclarationName: String by project
val projectGroup: String by project
val projectVersion: String by project

plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.6.10"
    `maven-publish`
    id("io.gitlab.arturbosch.detekt").version("1.20.0-RC2")
    jacoco
}

group = projectGroup
version = projectVersion

repositories {
    mavenCentral()
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

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
    compileOnly(gradleApi())
    implementation(project(":core"))
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
        jvmTarget = "11"
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

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}