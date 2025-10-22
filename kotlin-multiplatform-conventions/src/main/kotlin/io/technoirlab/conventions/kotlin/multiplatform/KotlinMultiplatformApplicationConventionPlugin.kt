package io.technoirlab.conventions.kotlin.multiplatform

import io.technoirlab.conventions.common.CommonConventionPlugin
import io.technoirlab.conventions.common.configuration.configureBuildConfig
import io.technoirlab.conventions.common.configuration.configureDetekt
import io.technoirlab.conventions.common.configuration.configureKotlinSerialization
import io.technoirlab.conventions.kotlin.multiplatform.api.KotlinMultiplatformApplicationExtension
import io.technoirlab.conventions.kotlin.multiplatform.configuration.configureBenchmarking
import io.technoirlab.conventions.kotlin.multiplatform.configuration.configureKotlinMultiplatform
import io.technoirlab.conventions.kotlin.multiplatform.configuration.configureMetro
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create

/**
 * Conventions for Kotlin Multiplatform application projects.
 *
 * DSL: [KotlinMultiplatformApplicationExtension]
 */
class KotlinMultiplatformApplicationConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val config = extensions.create<KotlinMultiplatformApplicationExtension>(KotlinMultiplatformApplicationExtension.NAME)
        config.initDefaults(project.name)

        pluginManager.apply(CommonConventionPlugin::class)

        afterEvaluate {
            configureBenchmarking(config.buildFeatures.benchmark)
            configureBuildConfig(config.buildFeatures.buildConfig, config.packageName)
            configureKotlinSerialization(config.buildFeatures.serialization)
            configureMetro(config.buildFeatures.metro)
        }

        configureKotlinMultiplatform(config, executable = true)
        configureDetekt()
    }
}
