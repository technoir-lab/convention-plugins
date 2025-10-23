package io.technoirlab.conventions.common.internal

import io.technoirlab.conventions.common.api.CommonExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Nested

abstract class CommonExtensionImpl(protected val project: Project) : CommonExtension {
    @get:Nested
    abstract override val buildFeatures: CommonBuildFeaturesImpl

    open fun initDefaults() {
        packageName.convention(project.name.replace('-', '.').replace('_', '.'))
        buildFeatures.initDefaults()
    }
}
