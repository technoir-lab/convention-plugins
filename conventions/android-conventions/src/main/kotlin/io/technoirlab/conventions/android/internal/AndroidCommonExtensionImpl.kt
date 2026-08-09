package io.technoirlab.conventions.android.internal

import io.technoirlab.conventions.android.api.AndroidCommonExtension
import io.technoirlab.conventions.common.internal.CommonExtensionImpl
import org.gradle.api.Project
import org.gradle.api.tasks.Nested

internal abstract class AndroidCommonExtensionImpl(project: Project) :
    CommonExtensionImpl(project),
    AndroidCommonExtension {
    @get:Nested
    abstract override val buildFeatures: AndroidBuildFeaturesImpl

    override fun initDefaults() {
        super.initDefaults()
        compileSdk.convention(DEFAULT_COMPILE_SDK)
        minSdk.convention(DEFAULT_MIN_SDK)
        metadata {
            name.convention(project.provider { project.name })
            description.convention(project.provider { project.description })
            developers.convention(emptyList())
            licenses.convention(emptyList())
        }
    }

    private companion object {
        private const val DEFAULT_COMPILE_SDK = 37
        private const val DEFAULT_MIN_SDK = 26
    }
}
