package io.technoirlab.conventions.android.api

/**
 * Marks Android test convention plugin DSL.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
internal annotation class AndroidTestDsl
