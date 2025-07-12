package com.iv127.kotlin.starter

import kotlin.test.Test
import kotlin.test.assertEquals

class ExtensionFunctionPrecedenceTest {

    class MyThing {
        @Suppress("detekt.FunctionOnlyReturningConstant")
        fun String.testMe(): String {
            return "MyThing testMe"
        }
    }

    @Suppress("detekt.FunctionOnlyReturningConstant")
    fun String.testMe(): String {
        return "Top level testMe"
    }

    @Test
    fun testPrecedence() {
        assertEquals("Top level testMe", "Hello!".testMe())
        with(MyThing()) { assertEquals("MyThing testMe", "Hello!".testMe()) }
    }

}
