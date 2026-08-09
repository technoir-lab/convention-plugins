package kmp.library

import kotlinx.cinterop.ExperimentalForeignApi

internal actual fun greet(name: String) {
    @OptIn(ExperimentalForeignApi::class)
    nativeGreet(name)
}
