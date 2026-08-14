package io.technoirlab.conventions.common

import io.technoirlab.conventions.common.api.CommonExtension
import io.technoirlab.conventions.common.configuration.configureCommon
import io.technoirlab.conventions.common.configuration.configureCoverage
import io.technoirlab.conventions.common.configuration.configureJava
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
        pluginManager.apply("com.squareup.sort-dependencies")

        val environment = Environment(providers)
        val projectSettings = ProjectSettingsImpl(this, environment)
        configureCommon(projectSettings)
        configureJava()
        configureCoverage()
        configureKtLint()
    }
}
