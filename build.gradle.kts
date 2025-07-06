plugins {
    id("java")
    kotlin("jvm")
    id("application")
}

group = "com.iv127.kotlin.starter"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.iv127.kotlin.starter.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-core:2.1.2")
    implementation("io.ktor:ktor-server-netty:2.1.2")
    implementation("ch.qos.logback:logback-classic:1.4.4")
    implementation("org.slf4j:slf4j-api:2.0.3")
    implementation("io.ktor:ktor-server-status-pages:2.1.2") // ktor dependency to use hooks like global exception handlers
    implementation("com.typesafe:config:1.4.2") // HOCON format *.conf reader
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.h2database:h2:2.1.214")
    implementation("org.flywaydb:flyway-core:9.5.1")
    implementation("com.github.seratch:kotliquery:1.9.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}