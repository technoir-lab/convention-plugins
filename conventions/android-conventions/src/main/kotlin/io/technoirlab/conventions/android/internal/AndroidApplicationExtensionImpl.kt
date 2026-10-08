package io.technoirlab.conventions.android.internal

import io.technoirlab.conventions.android.api.AndroidApplicationExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Nested

internal abstract class AndroidApplicationExtensionImpl(project: Project) :
    AndroidCommonExtensionImpl(project),
    AndroidApplicationExtension {
    @get:Nested
    abstract override val buildFeatures: AndroidBuildFeaturesImpl

    override fun initDefaults() {
        super.initDefaults()
        targetSdk.convention(DEFAULT_TARGET_SDK)
        versionCode.convention(DEFAULT_VERSION_CODE)
        versionName.convention(project.provider { project.version.toString() })
    }

    private companion object {
        private const val DEFAULT_TARGET_SDK = 37
        private const val DEFAULT_VERSION_CODE = 1
    }
}
