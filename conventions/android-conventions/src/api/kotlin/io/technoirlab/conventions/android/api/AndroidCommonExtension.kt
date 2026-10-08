package io.technoirlab.conventions.android.api

import io.technoirlab.conventions.common.api.CommonExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

/**
 * Shared configuration for Android projects.
 */
interface AndroidCommonExtension : CommonExtension {
    /**
     * Android API level used to compile the project. Defaults to 37.
     */
    val compileSdk: Property<Int>

    /**
     * Minimum Android API level supported by the project. Defaults to 26.
     */
    val minSdk: Property<Int>

    /**
     * Optional build features.
     */
    @get:Nested
    override val buildFeatures: AndroidBuildFeatures
}
