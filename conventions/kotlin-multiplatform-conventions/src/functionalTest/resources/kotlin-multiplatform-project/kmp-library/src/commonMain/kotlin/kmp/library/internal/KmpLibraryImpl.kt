package kmp.library.internal

import kmp.library.KmpLibrary
import kmp.library.greet

class KmpLibraryImpl : KmpLibrary {
    override fun hello(name: String) {
        greet(name)
    }

    // function placeholder
}
