package io.technoirlab.conventions.common.api

import io.technoirlab.conventions.common.api.metadata.ProjectMetadata
import org.gradle.api.Action
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

@CommonDsl
interface CommonExtension {
    /**
     * The base package name.
     * Defaults to sanitised project name, e.g. `jvm-library` -> `jvm.library`.
     */
    val packageName: Property<String>

    /**
     * Information about the project.
     */
    @get:Nested
    val metadata: ProjectMetadata

    /**
     * Information about the project.
     */
    fun metadata(configure: Action<ProjectMetadata>) {
        configure.execute(metadata)
    }

    /**
     * Optional build features.
     */
    @get:Nested
    val buildFeatures: CommonBuildFeatures

    fun initDefaults(projectName: String) {
        packageName.convention(projectName.replace('-', '.').replace('_', '.'))
        buildFeatures.initDefaults()
    }
}
