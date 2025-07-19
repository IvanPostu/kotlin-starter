package com.iv127.kotlin.starter.core


import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object ExampleAbcSpec : Spek({
    describe("ExampleAbc") {
        it("should greet in a right way") {
            val greetResult = ExampleAbc.greet("test1")
            assertEquals("Hello, test1!", greetResult)
        }
        xit("gonna be ignored") {
            assertEquals("1", "2")
        }
    }
})
