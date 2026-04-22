package io.technoirlab.conventions.common

import io.technoirlab.conventions.common.api.CommonExtension
import io.technoirlab.conventions.common.configuration.configureCommon
import io.technoirlab.conventions.common.configuration.configureDependencySorting
import io.technoirlab.conventions.common.configuration.configureKtLint
import io.technoirlab.gradle.Environment
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Common conventions for all projects.
 *
 * DSL: [CommonExtension]
 */
class CommonConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("org.gradle.lifecycle-base")

        val environment = Environment(providers)
        val projectSettings = ProjectSettingsImpl(this, environment)
        configureCommon(projectSettings)
        configureDependencySorting()
        configureKtLint()
    }
}
