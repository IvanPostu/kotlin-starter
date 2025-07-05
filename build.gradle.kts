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
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}