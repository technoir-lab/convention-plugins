package io.technoirlab.conventions.kotlin.multiplatform.internal

import io.technoirlab.conventions.kotlin.multiplatform.api.KotlinMultiplatformLibraryExtension
import org.gradle.api.Project

internal abstract class KotlinMultiplatformLibraryExtensionImpl(project: Project) :
    KotlinMultiplatformExtensionImpl(project),
    KotlinMultiplatformLibraryExtension
