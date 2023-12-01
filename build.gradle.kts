import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType

val overrideForPlainTermEnvironment = System.getenv("TERM")?.let { it == "dumb" } ?: true

plugins {
    kotlin("jvm") version "1.9.21"
    id("com.adarshr.test-logger") version "4.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

configure<TestLoggerExtension> {
    theme = if (overrideForPlainTermEnvironment) ThemeType.PLAIN_PARALLEL else ThemeType.MOCHA_PARALLEL

    showExceptions = false
}

tasks.test {
    useJUnitPlatform()

    reports {
        html.required.set(false)
        junitXml.required.set(false)
    }
}

kotlin {
    jvmToolchain(8)
}
