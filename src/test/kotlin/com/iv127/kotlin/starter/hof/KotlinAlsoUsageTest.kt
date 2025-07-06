package com.iv127.kotlin.starter.hof

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KotlinAlsoUsageTest {

    @Test
    fun `test kotlin also() usage`() {
        val intAsString = "1999".also {
            it + "a"
        }
        assertEquals("1999", intAsString)
    }

}