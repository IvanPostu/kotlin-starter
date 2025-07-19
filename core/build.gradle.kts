plugins {
    id("java")
    kotlin("jvm")
}

group = "com.iv127.kotlin.starter.core"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.19")
    testImplementation("org.spekframework.spek2:spek-runner-junit5:2.0.19")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
