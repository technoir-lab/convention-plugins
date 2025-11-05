package io.technoirlab.conventions.kotlin.multiplatform

import io.technoirlab.conventions.common.CommonConventionPlugin
import io.technoirlab.conventions.common.configuration.PublishingOptions
import io.technoirlab.conventions.common.configuration.configureBuildConfig
import io.technoirlab.conventions.common.configuration.configureCoverage
import io.technoirlab.conventions.common.configuration.configureDetekt
import io.technoirlab.conventions.common.configuration.configureDokka
import io.technoirlab.conventions.common.configuration.configureKotlinSerialization
import io.technoirlab.conventions.common.configuration.configurePublishing
import io.technoirlab.conventions.kotlin.multiplatform.api.KotlinMultiplatformLibraryExtension
import io.technoirlab.conventions.kotlin.multiplatform.configuration.configureBenchmarking
import io.technoirlab.conventions.kotlin.multiplatform.configuration.configureKotlinMultiplatform
import io.technoirlab.conventions.kotlin.multiplatform.configuration.configureMetro
import io.technoirlab.conventions.kotlin.multiplatform.internal.KotlinMultiplatformLibraryExtensionImpl
import io.technoirlab.gradle.Environment
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create

/**
 * Conventions for Kotlin Multiplatform library projects.
 *
 * DSL: [KotlinMultiplatformLibraryExtension]
 */
class KotlinMultiplatformLibraryConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val config = extensions.create(
            publicType = KotlinMultiplatformLibraryExtension::class,
            name = KotlinMultiplatformLibraryExtension.NAME,
            instanceType = KotlinMultiplatformLibraryExtensionImpl::class,
            project
        ) as KotlinMultiplatformLibraryExtensionImpl
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

        val environment = Environment(providers)
        val publishingOptions = PublishingOptions(
            componentName = "kotlin",
            publicationName = "kotlinMultiplatform"
        )

        configureKotlinMultiplatform(config)
        configureDetekt()
        configureDokka(environment)
        configureCoverage()
        configurePublishing(publishingOptions, config.metadata, environment)
    }
}
