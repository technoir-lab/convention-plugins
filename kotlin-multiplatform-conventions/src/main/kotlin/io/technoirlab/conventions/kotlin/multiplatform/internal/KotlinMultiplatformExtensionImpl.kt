package io.technoirlab.conventions.kotlin.multiplatform.internal

import io.technoirlab.conventions.common.internal.CommonExtensionImpl
import io.technoirlab.conventions.kotlin.multiplatform.api.KotlinMultiplatformExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Nested

internal abstract class KotlinMultiplatformExtensionImpl(project: Project) : CommonExtensionImpl(project), KotlinMultiplatformExtension {
    @get:Nested
    abstract override val buildFeatures: KotlinMultiplatformBuildFeaturesImpl
}
