package com.iv127.kotlin.starter.hof

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.awt.Point

class KotlinApplyUsageTest {

    @Test
    fun `test kotlin apply() usage`() {
        val point1 = Point()
        assertEquals(0.0, point1.getX())
        assertEquals(0.0, point1.getY())

        val point2 = Point().apply {
            this.x = 22
            this.y = 33
        }
        assertEquals(22.0, point2.getX())
        assertEquals(33.0, point2.getY())
    }

}