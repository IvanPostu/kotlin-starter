package com.iv127.kotlin.starter.hof

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KotlinFoldUsageTest {

    @Test
    fun `test kotlin fold() usage`() {
        val numbers = listOf(1, 2, 3, 4, 5)

        val concatenatedString = numbers.fold(StringBuilder()) { acc, number ->
            acc.append(number)
        }.toString()

        assertEquals("12345", concatenatedString)
    }

}
