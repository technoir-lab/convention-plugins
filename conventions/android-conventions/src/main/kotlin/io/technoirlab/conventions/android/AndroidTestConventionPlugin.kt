package io.technoirlab.conventions.android

import io.technoirlab.conventions.android.api.AndroidTestExtension
import io.technoirlab.conventions.android.configuration.configureAndroidTest
import io.technoirlab.conventions.android.configuration.configureBuildConfig
import io.technoirlab.conventions.android.configuration.configureKotlinParcelize
import io.technoirlab.conventions.android.internal.AndroidTestExtensionImpl
import io.technoirlab.conventions.common.configuration.configureKotlinSerialization
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create

/**
 * Conventions for Android test projects.
 *
 * DSL: [AndroidTestExtension]
 */
class AndroidTestConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val config = extensions.create(
            publicType = AndroidTestExtension::class,
            name = AndroidTestExtension.NAME,
            instanceType = AndroidTestExtensionImpl::class,
            project,
        ) as AndroidTestExtensionImpl
        config.initDefaults()

        pluginManager.apply("com.android.test")
        pluginManager.apply(AndroidCommonConventionPlugin::class)

        configureAndroidTest(config) { androidExtension ->
            configureBuildConfig(androidExtension, config.buildFeatures.buildConfig)
            configureKotlinParcelize(config.buildFeatures.parcelize)
            configureKotlinSerialization(config.buildFeatures.serialization)
        }
    }
}
