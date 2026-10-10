package io.technoirlab.conventions.jvm.api

/**
 * Marks JVM application convention plugin DSL.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
internal annotation class JvmApplicationDsl
