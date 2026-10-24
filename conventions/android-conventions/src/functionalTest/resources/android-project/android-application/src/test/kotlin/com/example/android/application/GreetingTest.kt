package com.example.android.application

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GreetingTest {
    @Test
    fun `test greeting`() {
        assertEquals("Hello, world!", greeting("world"))
    }
}
