package com.iv127.kotlin.starter

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue

class CoroutinesUsageTest {

    @Test
    fun `test coroutine with delay`() {
        val time = measureTimeMillis {
            runBlocking {
                val result = waitAndReturn(100L, "test")
                assertEquals("test", result)
            }
        }
        assertTrue(time >= 100L)
    }

    suspend fun <T> waitAndReturn(delayInMs: Long, result: T): T {
        delay(delayInMs)
        return result
    }

}
