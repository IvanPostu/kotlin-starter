package com.iv127.kotlin.starter.hof

import io.ktor.utils.io.core.Closeable
import io.ktor.utils.io.core.use
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KotlinUseUsageTest {

    companion object {
        private class TestCloseable : Closeable {
            private var closed = false

            fun isClosed() = this.closed

            override fun close() {
                this.closed = true
            }
        }
    }

    @Test
    fun `test kotlin use() usage`() {
        val toyCloseable = TestCloseable()
        assertFalse(toyCloseable.isClosed());
        toyCloseable.use {
            // no-op
        }
        assertTrue(toyCloseable.isClosed())
    }
}
