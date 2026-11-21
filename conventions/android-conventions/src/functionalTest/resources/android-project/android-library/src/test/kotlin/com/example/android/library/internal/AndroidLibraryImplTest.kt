package com.example.android.library.internal

import org.junit.jupiter.api.Test

class AndroidLibraryImplTest {
    private val androidLibrary = AndroidLibraryImpl()

    @Test
    fun `test hello`() {
        androidLibrary.hello("test")
    }

    @Test
    suspend fun `test hello from coroutine`() {
        androidLibrary.hello("test")
    }
}
