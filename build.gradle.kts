plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
    kotlin("jvm")
}

detekt {
    config.setFrom(files("detekt.yml"))
}

ktlint {
}

allprojects {
    repositories {
        mavenCentral()
    }
}

kotlin {
    jvmToolchain(17)
}
