package io.technoirlab.conventions.common.api

import org.gradle.api.Action
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

/**
 * Optional build features for all projects.
 */
interface CommonBuildFeatures {
    /**
     * Enable ABI validation. Disabled by default.
     */
    val abiValidation: Property<Boolean>

    /**
     * Configuration of `BuildConfig` class generation.
     */
    @get:Nested
    val buildConfig: BuildConfigSpec

    /**
     * Enable Kotlin serialization. Disabled by default.
     */
    val serialization: Property<Boolean>

    /**
     * Configuration of `BuildConfig` class generation.
     */
    fun buildConfig(configure: Action<BuildConfigSpec>) {
        configure.execute(buildConfig)
    }
}
