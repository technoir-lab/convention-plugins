package io.technoirlab.conventions.android.api

import org.gradle.api.Action
import org.gradle.api.provider.Property

/**
 * Configuration for Android test projects.
 */
@AndroidTestDsl
interface AndroidTestExtension : AndroidCommonExtension {
    /**
     * Unique test application identifier. Defaults to the package name.
     */
    val testApplicationId: Property<String>

    /**
     * Gradle path of the application project under test, e.g. `:app`.
     */
    val targetProjectPath: Property<String>

    /**
     * Android API level targeted by the test APK. Defaults to [compileSdk].
     */
    val targetSdk: Property<Int>

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
        const val NAME = "androidTest"
    }
}
