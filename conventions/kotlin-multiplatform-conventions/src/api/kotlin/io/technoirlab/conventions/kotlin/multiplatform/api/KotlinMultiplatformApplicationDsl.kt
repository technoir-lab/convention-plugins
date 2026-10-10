package io.technoirlab.conventions.kotlin.multiplatform.api

/**
 * Marks Kotlin Multiplatform application convention plugin DSL.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
internal annotation class KotlinMultiplatformApplicationDsl
