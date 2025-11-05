package com.example.jvm.library.internal

import com.example.jvm.library.JvmLibrary

class JvmLibraryImpl : JvmLibrary {
    override fun hello(name: String) {
        println("Hello, $name")
    }

    // function placeholder
}
