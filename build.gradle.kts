plugins {
    kotlin("jvm") version "2.1.0"
    application
}

group = "de.ad26.team07"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("de.ad26.team07.MainKt")
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.assertj:assertj-core:3.27.3")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
