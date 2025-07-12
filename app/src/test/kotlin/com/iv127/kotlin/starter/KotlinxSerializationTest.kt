package com.iv127.kotlin.starter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KotlinxSerializationTest {
    @Serializable
    data class MyThing(
        @SerialName("my_val") val myVal: String,
        val foo: Int
    )

    @Test
    fun `test serialization and deserialization`() {
        val obj =
            Json.decodeFromString<MyThing>(
                """{"my_val":"testing", "foo": 123}"""
            )
        assertEquals("testing", obj.myVal)
        assertEquals(123, obj.foo)

        val serialized = Json.encodeToString(obj)
        assertEquals("""{"my_val":"testing","foo":123}""", serialized)
    }
}
