package io.technoirlab.conventions.settings.api

/**
 * Marks settings convention plugin DSL.
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
internal annotation class SettingsDsl
