package io.technoirlab.conventions.android.api

import io.technoirlab.conventions.common.api.CommonBuildFeatures
import org.gradle.api.provider.Property

/**
 * Optional build features for Android projects.
 */
interface AndroidBuildFeatures : CommonBuildFeatures {
    /**
     * Enable the Kotlin Parcelize compiler plugin. Disabled by default.
     */
    val parcelize: Property<Boolean>
}
