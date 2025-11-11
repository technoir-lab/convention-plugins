package io.technoirlab.conventions.gradle.plugin.api

import io.technoirlab.conventions.common.api.CommonExtension
import org.gradle.api.Action
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

/**
 * Configuration for Gradle plugin projects.
 */
@GradlePluginDsl
interface GradlePluginExtension : CommonExtension {
    /**
     * Minimum Gradle version supported by the plugin. Defaults to `9.0`.
     */
    val minGradleVersion: Property<String>

    /**
     * Optional build features.
     */
    @get:Nested
    override val buildFeatures: GradlePluginBuildFeatures

    /**
     * Optional build features.
     */
    fun buildFeatures(action: Action<GradlePluginBuildFeatures>) {
        action.execute(buildFeatures)
    }

    /**
     * @suppress
     */
    companion object {
        const val NAME = "gradlePluginConfig"
    }
}
