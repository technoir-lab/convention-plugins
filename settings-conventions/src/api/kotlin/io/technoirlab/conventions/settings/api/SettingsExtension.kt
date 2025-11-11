package io.technoirlab.conventions.settings.api

import io.technoirlab.conventions.common.api.metadata.ProjectMetadata
import org.gradle.api.Action
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import java.net.URI

/**
 * Global configuration for all projects.
 */
@SettingsDsl
interface SettingsExtension {
    /**
     * Unique project ID. Required.
     */
    val projectId: Property<String>

    /**
     * The URL of the Develocity server. If not set,
     * build scans will be published to [scans.gradle.com](https://scans.gradle.com).
     */
    val develocityUrl: Property<URI>

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
     * @suppress
     */
    companion object {
        const val NAME = "globalSettings"
    }
}
