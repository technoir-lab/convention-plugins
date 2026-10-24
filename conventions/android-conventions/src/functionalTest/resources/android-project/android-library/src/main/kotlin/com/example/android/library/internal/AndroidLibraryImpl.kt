package com.example.android.library.internal

import com.example.android.library.AndroidLibrary

class AndroidLibraryImpl : AndroidLibrary {
    override fun hello(name: String) {
        println("Hello, $name")
    }
}
