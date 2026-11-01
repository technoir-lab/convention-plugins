package io.technoirlab.conventions.android.internal

import io.technoirlab.conventions.android.api.AndroidTestExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Nested

internal abstract class AndroidTestExtensionImpl(project: Project) :
    AndroidCommonExtensionImpl(project),
    AndroidTestExtension {
    @get:Nested
    abstract override val buildFeatures: AndroidBuildFeaturesImpl

    override fun initDefaults() {
        super.initDefaults()
        testApplicationId.convention(packageName)
        targetSdk.convention(compileSdk)
    }
}
