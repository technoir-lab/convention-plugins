package io.technoirlab.conventions.android.internal

import io.technoirlab.conventions.android.api.AndroidLibraryExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Nested

internal abstract class AndroidLibraryExtensionImpl(project: Project) :
    AndroidCommonExtensionImpl(project),
    AndroidLibraryExtension {
    @get:Nested
    abstract override val buildFeatures: AndroidLibraryBuildFeaturesImpl
}
