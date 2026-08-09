package io.technoirlab.conventions.kotlin.multiplatform.api

import io.technoirlab.conventions.common.api.CommonExtension
import org.gradle.api.Action
import org.gradle.api.tasks.Nested

/**
 * Configuration for Kotlin multiplatform projects.
 */
interface KotlinMultiplatformExtension : CommonExtension {
    /**
     * Optional build features.
     */
    @get:Nested
    override val buildFeatures: KotlinMultiplatformBuildFeatures

    /**
     * Optional build features.
     */
    fun buildFeatures(action: Action<KotlinMultiplatformBuildFeatures>) {
        action.execute(buildFeatures)
    }
}
