package com.example.android.test

import com.example.android.application.greeting
import org.junit.Assert.assertEquals
import org.junit.Test

class GreetingTest {
    @Test
    fun greetsWorld() {
        assertEquals("Hello, world!", greeting("world"))
    }
}
