package com.example.plugin.api

/**
 * Marks example convention plugin DSL.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
internal annotation class ExampleDsl
