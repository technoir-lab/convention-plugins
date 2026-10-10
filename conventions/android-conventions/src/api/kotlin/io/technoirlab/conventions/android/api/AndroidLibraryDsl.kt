package io.technoirlab.conventions.android.api

/**
 * Marks Android library convention plugin DSL.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
internal annotation class AndroidLibraryDsl
