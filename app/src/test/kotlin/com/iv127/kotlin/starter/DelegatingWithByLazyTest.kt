package com.iv127.kotlin.starter

import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class DelegatingWithByLazyTest {

    @Test
    fun testDelegatingWithByLazyTest() {
        val useLotsOfCpuCounter = AtomicInteger(0)
        val useLotsOfCpu: Runnable = kotlinx.coroutines.Runnable {
            useLotsOfCpuCounter.incrementAndGet()
        }

        class Foo(val myInput: String) {
            val expensiveThing: String by lazy {
                useLotsOfCpu.run()
                myInput
            }
        }

        val foo = Foo("abc")
        assertEquals("abc", foo.expensiveThing)
        assertEquals("abc", foo.expensiveThing)
        assertEquals("abc", foo.expensiveThing)
        assertEquals(1, useLotsOfCpuCounter.get())
    }

    @Test
    fun testDelegatingWithByRegularMethodTest() {
        val useLotsOfCpuCounter = AtomicInteger(0)
        val useLotsOfCpu: Runnable = kotlinx.coroutines.Runnable {
            useLotsOfCpuCounter.incrementAndGet()
        }

        class Foo(val myInput: String) {
            fun expensiveThing(): String {
                useLotsOfCpu.run()
                return myInput
            }
        }

        val foo = Foo("abc")
        assertEquals("abc", foo.expensiveThing())
        assertEquals("abc", foo.expensiveThing())
        assertEquals("abc", foo.expensiveThing())
        assertEquals(3, useLotsOfCpuCounter.get())
    }

}
