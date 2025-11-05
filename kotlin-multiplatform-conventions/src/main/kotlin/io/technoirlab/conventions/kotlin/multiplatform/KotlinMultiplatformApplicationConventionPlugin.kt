package io.technoirlab.conventions.kotlin.multiplatform

import io.technoirlab.conventions.common.CommonConventionPlugin
import io.technoirlab.conventions.common.configuration.configureBuildConfig
import io.technoirlab.conventions.common.configuration.configureCoverage
import io.technoirlab.conventions.common.configuration.configureDetekt
import io.technoirlab.conventions.common.configuration.configureKotlinSerialization
import io.technoirlab.conventions.kotlin.multiplatform.api.KotlinMultiplatformApplicationExtension
import io.technoirlab.conventions.kotlin.multiplatform.configuration.configureBenchmarking
import io.technoirlab.conventions.kotlin.multiplatform.configuration.configureKotlinMultiplatform
import io.technoirlab.conventions.kotlin.multiplatform.configuration.configureMetro
import io.technoirlab.conventions.kotlin.multiplatform.internal.KotlinMultiplatformApplicationExtensionImpl
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
        val config = extensions.create(
            publicType = KotlinMultiplatformApplicationExtension::class,
            name = KotlinMultiplatformApplicationExtension.NAME,
            instanceType = KotlinMultiplatformApplicationExtensionImpl::class,
            project
        ) as KotlinMultiplatformApplicationExtensionImpl
        config.initDefaults()

        pluginManager.apply(CommonConventionPlugin::class)

        afterEvaluate {
            configureBenchmarking(config.buildFeatures.benchmark)
            configureBuildConfig(config.buildFeatures.buildConfig, config.packageName)
            configureKotlinSerialization(config.buildFeatures.serialization)
            configureMetro(config.buildFeatures.metro)
        }

        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("org.jetbrains.kotlinx.kover")

        configureKotlinMultiplatform(config, executable = true)
        configureDetekt()
        configureCoverage()
    }
}
