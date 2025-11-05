package io.technoirlab.conventions.jvm

import io.technoirlab.conventions.common.CommonConventionPlugin
import io.technoirlab.conventions.common.configuration.DocsFormat
import io.technoirlab.conventions.common.configuration.PublishingOptions
import io.technoirlab.conventions.common.configuration.configureBuildConfig
import io.technoirlab.conventions.common.configuration.configureCoverage
import io.technoirlab.conventions.common.configuration.configureDetekt
import io.technoirlab.conventions.common.configuration.configureDokka
import io.technoirlab.conventions.common.configuration.configureJava
import io.technoirlab.conventions.common.configuration.configureKotlin
import io.technoirlab.conventions.common.configuration.configureKotlinSerialization
import io.technoirlab.conventions.common.configuration.configurePublishing
import io.technoirlab.conventions.common.configuration.configureTestFixtures
import io.technoirlab.conventions.common.configuration.configureTesting
import io.technoirlab.conventions.jvm.api.JvmLibraryExtension
import io.technoirlab.conventions.jvm.internal.JvmLibraryExtensionImpl
import io.technoirlab.gradle.Environment
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create

/**
 * Conventions for JVM library projects.
 *
 * DSL: [JvmLibraryExtension]
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val config = extensions.create(
            publicType = JvmLibraryExtension::class,
            name = JvmLibraryExtension.NAME,
            instanceType = JvmLibraryExtensionImpl::class,
            project
        ) as JvmLibraryExtensionImpl
        config.initDefaults()

        pluginManager.apply(CommonConventionPlugin::class)

        afterEvaluate {
            configureBuildConfig(config.buildFeatures.buildConfig, config.packageName)
            configureKotlinSerialization(config.buildFeatures.serialization)
        }

        pluginManager.apply("java-library")
        pluginManager.apply("org.jetbrains.kotlin.jvm")
        pluginManager.apply("org.jetbrains.kotlin.plugin.sam.with.receiver")
        pluginManager.apply("org.jetbrains.kotlinx.kover")

        val environment = Environment(providers)
        val publishingOptions = PublishingOptions(
            componentName = "java",
            publicationName = "libraryMaven",
            docsFormats = setOf(DocsFormat.Javadoc)
        )

        configureJava()
        configureKotlin(enableAbiValidation = config.buildFeatures.abiValidation)
        configureDetekt()
        configureDokka(environment, DocsFormat.All)
        configurePublishing(publishingOptions, config.metadata, environment)
        configureTesting()
        configureTestFixtures()
        configureCoverage()
    }
}
