package com.iv127.kotlin.starter

import com.google.gson.Gson
import kotlin.test.Test
import kotlin.test.assertEquals

class InlineWithReifiedTest {

    @Test
    fun testInlineWithReifiedFunction() {
        val deserialized1: Map<String, String> = this.deserializeJson("{\"a\": \"cde\"}")
        val deserialized2: Person = this.deserializeJson("{\"name\": \"boba\"}")
        assertEquals(mapOf(Pair("a", "cde")), deserialized1)
        assertEquals(Person("boba"), deserialized2)
    }

    private inline fun <reified T> deserializeJson(json: String): T {
        return Gson().fromJson(json, T::class.java)
    }

    private data class Person(val name: String)
}
