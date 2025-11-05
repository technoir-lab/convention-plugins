package io.technoirlab.conventions.root

import io.technoirlab.conventions.common.CommonConventionPlugin
import io.technoirlab.conventions.common.configuration.configureCoverage
import io.technoirlab.conventions.root.configuration.configureDokka
import io.technoirlab.conventions.root.configuration.configurePublishing
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class RootConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        check(path == ":") { "Root convention plugin must be applied to the root project." }

        pluginManager.apply(CommonConventionPlugin::class)
        pluginManager.apply("org.jetbrains.kotlinx.kover")

        configureDokka()
        configureCoverage()
        configurePublishing()
    }
}
