package kmp.library.internal

import kotlin.test.Test

class KmpLibraryImplTest {
    private val kmpLibrary = KmpLibraryImpl()

    @Test
    fun `test hello`() {
        kmpLibrary.hello("test")
    }
}
