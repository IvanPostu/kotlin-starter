package com.iv127.kotlin.starter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test

class JavaPlatformTypeGetIntegerTest {

    private val javaPlatformTypeGetInteger: JavaPlatformTypeGetInteger = JavaPlatformTypeGetInteger()

    @Test
    fun `test with non null return value`() {
        val result = javaPlatformTypeGetInteger.getInteger(12)
        assertEquals(12, result)
    }

    @Test
    fun `test with null return value`() {
        val e: NullPointerException = assertThrowsExactly(NullPointerException::class.java, {
            val result: Int = javaPlatformTypeGetInteger.getInteger(0)
            println(result) // result will either be non-null or won't reach this line!
        })
        assertEquals("getInteger(...) must not be null", e.message)
    }

}
