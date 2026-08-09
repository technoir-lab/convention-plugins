package io.technoirlab.conventions.android

import io.technoirlab.conventions.android.api.AndroidLibraryExtension
import io.technoirlab.conventions.android.configuration.RELEASE_VARIANT
import io.technoirlab.conventions.android.configuration.configureAndroidDokka
import io.technoirlab.conventions.android.configuration.configureAndroidLibrary
import io.technoirlab.conventions.android.configuration.configureBuildConfig
import io.technoirlab.conventions.android.configuration.configureKotlinParcelize
import io.technoirlab.conventions.android.internal.AndroidLibraryExtensionImpl
import io.technoirlab.conventions.common.configuration.DocsFormat
import io.technoirlab.conventions.common.configuration.PublishingOptions
import io.technoirlab.conventions.common.configuration.configureDokka
import io.technoirlab.conventions.common.configuration.configureKotlinSerialization
import io.technoirlab.conventions.common.configuration.configurePublishing
import io.technoirlab.gradle.Environment
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create

/**
 * Conventions for Android library projects.
 *
 * DSL: [AndroidLibraryExtension]
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val config = extensions.create(
            publicType = AndroidLibraryExtension::class,
            name = AndroidLibraryExtension.NAME,
            instanceType = AndroidLibraryExtensionImpl::class,
            project,
        ) as AndroidLibraryExtensionImpl
        config.initDefaults()

        pluginManager.apply("com.android.library")
        pluginManager.apply(AndroidCommonConventionPlugin::class)

        val environment = Environment(providers)
        val publishingOptions = PublishingOptions(
            componentName = RELEASE_VARIANT,
            publicationName = "libraryMaven",
            docsFormats = setOf(DocsFormat.Javadoc),
        )

        configureDokka(environment, DocsFormat.All)
        configureAndroidDokka()
        configurePublishing(publishingOptions, config.metadata, environment)

        configureAndroidLibrary(config) { androidExtension ->
            configureKotlinParcelize(config.buildFeatures.parcelize)
            configureKotlinSerialization(config.buildFeatures.serialization)
            configureBuildConfig(androidExtension, config.buildFeatures.buildConfig)
        }
    }
}
