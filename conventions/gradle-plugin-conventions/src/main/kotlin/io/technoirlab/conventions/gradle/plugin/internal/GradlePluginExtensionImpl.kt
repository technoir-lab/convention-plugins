package io.technoirlab.conventions.gradle.plugin.internal

import io.technoirlab.conventions.common.internal.CommonExtensionImpl
import io.technoirlab.conventions.gradle.plugin.api.GradlePluginExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Nested

internal abstract class GradlePluginExtensionImpl(project: Project) :
    CommonExtensionImpl(project),
    GradlePluginExtension {

    @get:Nested
    abstract override val buildFeatures: GradlePluginBuildFeaturesImpl

    override fun initDefaults() {
        super.initDefaults()
        minGradleVersion.convention("9.0")
    }
}
