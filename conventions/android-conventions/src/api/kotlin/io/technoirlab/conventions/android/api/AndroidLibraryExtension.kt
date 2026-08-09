package io.technoirlab.conventions.android.api

import org.gradle.api.Action
import org.gradle.api.tasks.Nested

/**
 * Configuration for Android library projects.
 */
@AndroidLibraryDsl
interface AndroidLibraryExtension : AndroidCommonExtension {
    /**
     * Optional build features.
     */
    @get:Nested
    override val buildFeatures: AndroidLibraryBuildFeatures

    /**
     * Configures optional build features.
     */
    fun buildFeatures(action: Action<AndroidLibraryBuildFeatures>) {
        action.execute(buildFeatures)
    }

    /**
     * @suppress
     */
    companion object {
        const val NAME = "androidLibrary"
    }
}
