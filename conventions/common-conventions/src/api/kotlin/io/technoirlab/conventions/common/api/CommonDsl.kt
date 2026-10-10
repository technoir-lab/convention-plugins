package io.technoirlab.conventions.common.api

/**
 * Marks common convention plugin DSL.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
internal annotation class CommonDsl
