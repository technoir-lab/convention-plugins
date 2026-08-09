package io.technoirlab.conventions.android

import io.technoirlab.conventions.android.configuration.configureTesting
import io.technoirlab.conventions.common.CommonConventionPlugin
import io.technoirlab.conventions.common.configuration.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * Common conventions for Android projects.
 */
class AndroidCommonConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply(CommonConventionPlugin::class)
        pluginManager.apply("org.jetbrains.kotlinx.kover")
        pluginManager.apply("com.google.devtools.ksp")
        pluginManager.apply("org.jlleitschuh.gradle.ktlint")

        configureKotlin()
        configureTesting()
    }
}
