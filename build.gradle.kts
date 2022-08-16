import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    kotlin("jvm") version(libs.versions.kotlin)
    alias(libs.plugins.detekt)
    `java-gradle-plugin`
    `maven-publish`
    jacoco
    kotlin("kapt") version(libs.versions.kotlin)
    id("com.gradle.plugin-publish") version "1.0.0-rc-1"
}

group = pluginGroupPackageName
version = pluginVersion

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.mockk)

    compileOnly(gradleApi())

    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.oshi)
    implementation(libs.sqlite.driver)
    implementation(libs.mysql.driver)
    implementation(libs.jetbrains.exposed.core)
    implementation(libs.jetbrains.exposed.jdbc)
    implementation(libs.moshi)
    kapt(libs.moshi.codegen)
    implementation(libs.commons.io)
    implementation(libs.coroutines)
    testImplementation(libs.coroutines.test)
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
                "detekt",
                "build",
                "test",
                "publishToMavenLocal"
            ).args("--info")
        }
    }
}

detekt {
    config = files("detekt-config.yml")
    buildUponDefaultConfig = true
    source = files(
        "src/main/kotlin"
    )
}
