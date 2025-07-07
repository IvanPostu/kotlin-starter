plugins {
    id("java")
    kotlin("jvm")
}

group = "com.iv127.kotlin.starter.core"
version = "1.0-SNAPSHOT"

dependencies {
}

tasks.test {
    useJUnitPlatform()
}
