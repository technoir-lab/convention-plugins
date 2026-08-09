package io.technoirlab.conventions.kotlin.multiplatform.internal

import io.technoirlab.conventions.kotlin.multiplatform.api.KotlinMultiplatformApplicationExtension
import org.gradle.api.Project

internal abstract class KotlinMultiplatformApplicationExtensionImpl(project: Project) :
    KotlinMultiplatformExtensionImpl(project),
    KotlinMultiplatformApplicationExtension
