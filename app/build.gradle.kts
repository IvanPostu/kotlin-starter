plugins {
    id("java")
    kotlin("jvm")
    id("application")
    kotlin("plugin.serialization") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.iv127.kotlin.starter.app"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.iv127.kotlin.starter.app.Application")
}

val ktorVersion = "2.2.0"

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.4.4")
    implementation("org.slf4j:slf4j-api:2.0.3")
    // ktor dependency to use hooks like global exception handlers
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("com.typesafe:config:1.4.2") // HOCON format *.conf reader
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.h2database:h2:2.1.214")
    implementation("org.flywaydb:flyway-core:9.5.1")
    implementation("com.github.seratch:kotliquery:1.9.0")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.1.2")
    implementation("io.arrow-kt:arrow-fx-stm:1.1.2")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("at.favre.lib:bcrypt:0.9.0")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")

    // aws lambda lib (instead of ktor)
    // implementation("com.amazonaws:aws-lambda-java-core:1.2.1")

    implementation("org.springframework:spring-context:5.3.23")
    implementation("io.ktor:ktor-server-servlet:$ktorVersion")
    implementation("org.jetbrains.kotlinx:atomicfu:0.23.2")
    implementation("org.eclipse.jetty:jetty-server:9.4.49.v20220914")
    implementation("org.eclipse.jetty:jetty-servlet:9.4.49.v20220914")
    implementation("org.springframework.security:spring-security-web:5.7.3")
    implementation("org.springframework.security:spring-security-config:5.7.3")

    // java analogue of ktor
    implementation("io.jooby:jooby:2.16.1")
    implementation("io.jooby:jooby-netty:2.16.1")

    implementation(project(":core"))
}

tasks.test {
    useJUnitPlatform()
}
