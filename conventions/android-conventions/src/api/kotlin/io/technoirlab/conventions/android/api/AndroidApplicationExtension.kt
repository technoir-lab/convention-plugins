package io.technoirlab.conventions.android.api

import org.gradle.api.Action
import org.gradle.api.provider.Property

/**
 * Configuration for Android application projects.
 */
@AndroidApplicationDsl
interface AndroidApplicationExtension : AndroidCommonExtension {
    /**
     * Unique application identifier.
     */
    val applicationId: Property<String>

    /**
     * Android API level targeted by the application. Defaults to 37.
     */
    val targetSdk: Property<Int>

    /**
     * Internal application version number. Defaults to 1.
     */
    val versionCode: Property<Int>

    /**
     * User-visible application version. Defaults to the project version.
     */
    val versionName: Property<String>

    /**
     * Configures optional build features.
     */
    fun buildFeatures(action: Action<AndroidBuildFeatures>) {
        action.execute(buildFeatures)
    }

    /**
     * @suppress
     */
    companion object {
        const val NAME = "androidApplication"
    }
}
