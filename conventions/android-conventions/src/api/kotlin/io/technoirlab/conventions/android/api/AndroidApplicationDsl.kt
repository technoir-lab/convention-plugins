package io.technoirlab.conventions.android.api

/**
 * Marks Android application convention plugin DSL.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
internal annotation class AndroidApplicationDsl
