plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
    kotlin("jvm")
}

ktlint {
}

allprojects {
    repositories {
        mavenCentral()
    }
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    detekt {
        config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    }
}

kotlin {
    jvmToolchain(17)
}
