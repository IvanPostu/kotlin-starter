package com.iv127.kotlin.starter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.random.Random

class StringExtensionFunctionTest {

    @Test
    fun `test extension function getRandomLetter() on String class`() {
        fun String.getRandomLetter() =
            this[Random.nextInt(this.length)]

        val c = "aaa".getRandomLetter()
        assertEquals('a', c)
    }

    @Test
    fun `test extension function with lambda with a receiver type`() {
        fun String.transformRandomLetter(
            body: String.() -> String
        ): String {
            val range = Random.nextInt(this.length).let {
                it.rangeTo(it)
            }
            return this.replaceRange(
                range,
                this.substring(range).body()
            )
        }

        val example = "FooBarBaz".transformRandomLetter {
            "***${this.uppercase()}***"
        }
        assertTrue(example.contains("***"))
    }

}