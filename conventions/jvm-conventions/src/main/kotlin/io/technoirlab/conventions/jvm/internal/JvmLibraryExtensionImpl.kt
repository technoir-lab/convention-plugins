package io.technoirlab.conventions.jvm.internal

import io.technoirlab.conventions.common.internal.CommonExtensionImpl
import io.technoirlab.conventions.jvm.api.JvmLibraryExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Nested

internal abstract class JvmLibraryExtensionImpl(project: Project) :
    CommonExtensionImpl(project),
    JvmLibraryExtension {

    @get:Nested
    abstract override val buildFeatures: JvmBuildFeaturesImpl
}
