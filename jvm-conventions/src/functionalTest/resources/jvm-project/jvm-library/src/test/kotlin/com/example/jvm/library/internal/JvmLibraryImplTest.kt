package com.example.jvm.library.internal

import org.junit.jupiter.api.Test

class JvmLibraryImplTest {
    private val jvmLibrary = JvmLibraryImpl()

    @Test
    fun `test hello`() {
        jvmLibrary.hello("test")
    }
}
