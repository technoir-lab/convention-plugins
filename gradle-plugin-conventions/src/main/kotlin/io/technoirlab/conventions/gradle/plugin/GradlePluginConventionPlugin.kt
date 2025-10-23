package io.technoirlab.conventions.gradle.plugin

import io.technoirlab.conventions.common.CommonConventionPlugin
import io.technoirlab.conventions.common.configuration.DocsFormat
import io.technoirlab.conventions.common.configuration.PublishingOptions
import io.technoirlab.conventions.common.configuration.configureBuildConfig
import io.technoirlab.conventions.common.configuration.configureDetekt
import io.technoirlab.conventions.common.configuration.configureDokka
import io.technoirlab.conventions.common.configuration.configureJava
import io.technoirlab.conventions.common.configuration.configureKotlin
import io.technoirlab.conventions.common.configuration.configureKotlinSerialization
import io.technoirlab.conventions.common.configuration.configurePublishing
import io.technoirlab.conventions.common.configuration.configureTestFixtures
import io.technoirlab.conventions.common.configuration.configureTesting
import io.technoirlab.conventions.gradle.plugin.api.GradlePluginExtension
import io.technoirlab.conventions.gradle.plugin.configuration.configureDependencyAnalysis
import io.technoirlab.conventions.gradle.plugin.configuration.configurePlugin
import io.technoirlab.conventions.gradle.plugin.configuration.embeddedKotlinVersion
import io.technoirlab.conventions.gradle.plugin.configuration.kotlinLanguageVersion
import io.technoirlab.conventions.gradle.plugin.internal.GradlePluginExtensionImpl
import io.technoirlab.gradle.Environment
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.util.GradleVersion

/**
 * Conventions for Gradle plugin projects.
 *
 * DSL: [GradlePluginExtension]
 */
class GradlePluginConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val config = extensions.create(
            publicType = GradlePluginExtension::class,
            name = GradlePluginExtension.NAME,
            instanceType = GradlePluginExtensionImpl::class,
            project
        ) as GradlePluginExtensionImpl
        config.initDefaults()

        pluginManager.apply(CommonConventionPlugin::class)

        afterEvaluate {
            configureBuildConfig(config.buildFeatures.buildConfig, config.packageName)
            configureKotlinSerialization(config.buildFeatures.serialization)
        }

        pluginManager.apply("java-gradle-plugin")
        pluginManager.apply("org.jetbrains.kotlin.jvm")
        pluginManager.apply("org.jetbrains.kotlin.plugin.sam.with.receiver")

        val gradleVersion = config.minGradleVersion.map { GradleVersion.version(it) }
        val environment = Environment(providers)
        val publishingOptions = PublishingOptions(
            componentName = "java",
            publicationName = "pluginMaven",
            docsFormats = setOf(DocsFormat.Javadoc)
        )

        configureJava()
        configureKotlin(
            kotlinVersion = gradleVersion.map { it.kotlinLanguageVersion },
            stdlibVersion = gradleVersion.map { it.embeddedKotlinVersion },
            enableAbiValidation = config.buildFeatures.abiValidation
        )
        configureDetekt()
        configureDokka(environment, DocsFormat.All)
        configurePublishing(publishingOptions, config.metadata, environment) {
            suppressPomMetadataWarningsFor("apiElements")
            suppressPomMetadataWarningsFor("runtimeElements")
            suppressPomMetadataWarningsFor("apiApiElements")
            suppressPomMetadataWarningsFor("apiRuntimeElements")
            suppressPomMetadataWarningsFor("apiJavadocElements")
            suppressPomMetadataWarningsFor("apiSourcesElements")
        }
        configurePlugin(config, environment)
        configureTesting()
        configureTestFixtures()
        configureDependencyAnalysis()
    }
}
