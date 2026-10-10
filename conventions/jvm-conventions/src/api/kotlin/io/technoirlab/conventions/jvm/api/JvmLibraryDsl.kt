package io.technoirlab.conventions.jvm.api

/**
 * Marks JVM library convention plugin DSL.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
internal annotation class JvmLibraryDsl
