package io.technoirlab.conventions.gradle.plugin.api

/**
 * Marks Gradle plugin convention plugin DSL.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
internal annotation class GradlePluginDsl
