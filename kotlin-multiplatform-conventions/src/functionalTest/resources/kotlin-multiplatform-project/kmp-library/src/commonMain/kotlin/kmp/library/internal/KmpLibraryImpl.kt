package kmp.library.internal

import kmp.library.KmpLibrary
import kmp.library.nativeGreet

class KmpLibraryImpl : KmpLibrary {
    override fun hello(name: String) {
        nativeGreet(name)
    }
}
