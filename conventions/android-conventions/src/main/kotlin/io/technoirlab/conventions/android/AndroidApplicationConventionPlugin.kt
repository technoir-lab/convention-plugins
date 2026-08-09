package io.technoirlab.conventions.android

import io.technoirlab.conventions.android.api.AndroidApplicationExtension
import io.technoirlab.conventions.android.configuration.configureAndroidApplication
import io.technoirlab.conventions.android.configuration.configureBuildConfig
import io.technoirlab.conventions.android.configuration.configureKotlinParcelize
import io.technoirlab.conventions.android.internal.AndroidApplicationExtensionImpl
import io.technoirlab.conventions.common.configuration.configureKotlinSerialization
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create

/**
 * Conventions for Android application projects.
 *
 * DSL: [AndroidApplicationExtension]
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val config = extensions.create(
            publicType = AndroidApplicationExtension::class,
            name = AndroidApplicationExtension.NAME,
            instanceType = AndroidApplicationExtensionImpl::class,
            project,
        ) as AndroidApplicationExtensionImpl
        config.initDefaults()

        pluginManager.apply("com.android.application")
        pluginManager.apply(AndroidCommonConventionPlugin::class)

        configureAndroidApplication(config) { androidExtension ->
            configureBuildConfig(androidExtension, config.buildFeatures.buildConfig)
            configureKotlinParcelize(config.buildFeatures.parcelize)
            configureKotlinSerialization(config.buildFeatures.serialization)
        }
    }
}
