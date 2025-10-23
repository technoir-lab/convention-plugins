package io.technoirlab.conventions.jvm.internal

import io.technoirlab.conventions.common.internal.CommonExtensionImpl
import io.technoirlab.conventions.jvm.api.JvmApplicationExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Nested

internal abstract class JvmApplicationExtensionImpl(project: Project) :
    CommonExtensionImpl(project),
    JvmApplicationExtension {

    @get:Nested
    abstract override val buildFeatures: JvmBuildFeaturesImpl
}
