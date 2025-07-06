package com.iv127.kotlin.starter.hof

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class KotlinLetUsageTest {

    @Test
    fun `test kotlin let works like Java Stream map method`() {
        val intAsString = "1999"
        val intAsIntegerPlusOne = intAsString.let {
            val num = Integer.valueOf(it)
            num + 1
        }
        assertEquals(2000, intAsIntegerPlusOne)
    }

    @Test
    fun `test kotlin let is executed only if nullable value is present`() {
        val example: Int? = getNullable(true)
        example?.let {
            fail("It will never happen")
        }

        val example2: Int? = getNullable(false)?.let {
            it + 1
        }
        assertEquals(2, example2)
    }

    @Test
    fun `test kotlin let with explicit default`() {
        val example1 = getNullable(true)?.let {
            it + 1
        } ?: 99
        assertEquals(99, example1)

        val example2 = getNullable(true)?.let {
            it + 1
        } ?: run {
            val q = 99
            q + 10
        }
        assertEquals(109, example2)
    }

    fun getNullable(returnNull: Boolean): Int? {
        return if (returnNull) null else 1
    }

}