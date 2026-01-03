package io.technoirlab.conventions.settings

import io.technoirlab.conventions.common.api.CommonExtension
import io.technoirlab.conventions.common.api.metadata.ProjectMetadata
import io.technoirlab.conventions.settings.api.SettingsExtension
import io.technoirlab.conventions.settings.configuration.configureDependencyAnalysis
import io.technoirlab.conventions.settings.configuration.configureDependencyResolution
import io.technoirlab.conventions.settings.configuration.configureDevelocity
import io.technoirlab.conventions.settings.configuration.configurePublishing
import io.technoirlab.gradle.Environment
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType

@Suppress("UnstableApiUsage")
class SettingsConventionPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) = with(settings) {
        pluginManager.apply("com.autonomousapps.build-health")
        pluginManager.apply("com.gradle.develocity")
        pluginManager.apply("org.gradle.toolchains.foojay-resolver-convention")
        pluginManager.apply("com.gradleup.nmcp.settings")

        val config = extensions.create<SettingsExtension>(SettingsExtension.NAME)

        gradle.settingsEvaluated {
            rootProject.name = config.projectId.get()
        }

        gradle.lifecycle.beforeProject {
            configureMetadata(config.metadata)
        }

        gradle.lifecycle.afterProject {
            configureDependencyResolution()
        }

        val environment = Environment(providers)
        configureDependencyAnalysis()
        configureDependencyResolution(environment)
        configureDevelocity(config, environment)
        configurePublishing()
    }

    private fun Project.configureMetadata(metadata: ProjectMetadata) {
        pluginManager.withPlugin("io.technoirlab.conventions.common") {
            extensions.findByType(CommonExtension::class)?.metadata?.initWith(metadata)
        }
    }

    private fun ProjectMetadata.initWith(other: ProjectMetadata) {
        name.convention(other.name)
        description.convention(other.description)
        url.convention(other.url)
        licenses.convention(other.licenses)
        developers.convention(other.developers)
    }
}
