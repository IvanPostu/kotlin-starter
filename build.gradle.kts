plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    // https://github.com/JLLeitschuh/ktlint-gradle?tab=readme-ov-file#simple-setup
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
    kotlin("jvm")
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set("0.50.0")
    debug.set(true)
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
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

dependencies {
    runtimeOnly("com.pinterest:ktlint:0.50.0")
}

kotlin {
    jvmToolchain(11)
}
