plugins {
    id("java")
    kotlin("jvm")
    id("application")
    kotlin("plugin.serialization") version "2.0.21"
}

group = "com.iv127.kotlin.starter.app"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.iv127.kotlin.starter.app.Application")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-core:2.1.2")
    implementation("io.ktor:ktor-server-netty:2.1.2")
    implementation("ch.qos.logback:logback-classic:1.4.4")
    implementation("org.slf4j:slf4j-api:2.0.3")
    // ktor dependency to use hooks like global exception handlers
    implementation("io.ktor:ktor-server-status-pages:2.1.2")
    implementation("com.typesafe:config:1.4.2") // HOCON format *.conf reader
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.h2database:h2:2.1.214")
    implementation("org.flywaydb:flyway-core:9.5.1")
    implementation("com.github.seratch:kotliquery:1.9.0")
    implementation("io.ktor:ktor-client-core:2.1.2")
    implementation("io.ktor:ktor-client-cio:2.1.2")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.1.2")
    implementation("io.arrow-kt:arrow-fx-stm:1.1.2")
    implementation("io.ktor:ktor-server-html-builder:2.1.2")
    implementation("at.favre.lib:bcrypt:0.9.0")
    implementation("io.ktor:ktor-server-auth:2.1.2")
    implementation("io.ktor:ktor-server-sessions:2.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("io.ktor:ktor-server-cors-jvm:2.1.2")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.1.2")

    implementation(project(":core"))
}

tasks.test {
    useJUnitPlatform()
}
