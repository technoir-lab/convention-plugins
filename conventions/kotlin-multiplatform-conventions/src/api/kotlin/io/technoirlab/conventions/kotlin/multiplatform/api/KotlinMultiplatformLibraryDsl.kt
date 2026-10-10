package io.technoirlab.conventions.kotlin.multiplatform.api

/**
 * Marks Kotlin Multiplatform library convention plugin DSL.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
internal annotation class KotlinMultiplatformLibraryDsl
