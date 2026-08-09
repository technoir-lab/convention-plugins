package io.technoirlab.conventions.android.api

import org.gradle.api.provider.Property

/**
 * Optional build features for Android library projects.
 */
interface AndroidLibraryBuildFeatures : AndroidBuildFeatures {
    /**
     * Enable Android test fixtures. Disabled by default.
     */
    val testFixtures: Property<Boolean>
}
