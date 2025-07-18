package com.iv127.kotlin.starter

import org.junit.jupiter.api.Assertions.assertThrowsExactly
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KotlinNotNullCustomContractTest {

    @Test
    fun testKotlinNotNullCustomContractThrowsNullPointerTest() {
        val foo: Foo? = Foo.create(false)
        assertThrowsExactly(NullPointerException::class.java, {
            customAssertNotNull(foo)
        })
        assertNull(foo)
    }

    @Test
    fun testKotlinNotNullCustomContractTest() {
        val foo: Foo? = Foo.create(true)
        customAssertNotNull(foo)
        assertEquals("test", foo.name)
    }

    @OptIn(ExperimentalContracts::class)
    private fun <T : Any> customAssertNotNull(actual: T?): T {
        contract { returns() implies (actual != null) }
        return actual!!
    }

    private data class Foo(val name: String) {
        companion object {
            fun create(indeedCreate: Boolean): Foo? {
                if (indeedCreate) {
                    return Foo("test")
                }
                return null
            }
        }
    }
}
