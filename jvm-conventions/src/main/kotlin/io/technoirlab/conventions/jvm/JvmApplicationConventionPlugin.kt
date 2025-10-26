package io.technoirlab.conventions.jvm

import io.technoirlab.conventions.common.CommonConventionPlugin
import io.technoirlab.conventions.common.configuration.configureBuildConfig
import io.technoirlab.conventions.common.configuration.configureDetekt
import io.technoirlab.conventions.common.configuration.configureJava
import io.technoirlab.conventions.common.configuration.configureKotlin
import io.technoirlab.conventions.common.configuration.configureKotlinSerialization
import io.technoirlab.conventions.common.configuration.configureKtLint
import io.technoirlab.conventions.common.configuration.configureTesting
import io.technoirlab.conventions.jvm.api.JvmApplicationExtension
import io.technoirlab.conventions.jvm.internal.JvmApplicationExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

/**
 * Conventions for JVM application projects.
 *
 * DSL: [JvmApplicationExtension]
 */
class JvmApplicationConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val config = extensions.create(
            publicType = JvmApplicationExtension::class,
            name = JvmApplicationExtension.NAME,
            instanceType = JvmApplicationExtensionImpl::class,
            project
        ) as JvmApplicationExtensionImpl
        config.initDefaults()

        pluginManager.apply(CommonConventionPlugin::class)

        afterEvaluate {
            configureBuildConfig(config.buildFeatures.buildConfig, config.packageName)
            configureKotlinSerialization(config.buildFeatures.serialization)
        }

        pluginManager.apply("application")
        pluginManager.apply("org.jetbrains.kotlin.jvm")
        pluginManager.apply("org.jetbrains.kotlin.plugin.sam.with.receiver")

        configureJava()
        configureApplication(config)
        configureKotlin(enableAbiValidation = config.buildFeatures.abiValidation)
        configureDetekt()
        configureKtLint()
        configureTesting()
    }

    private fun Project.configureApplication(config: JvmApplicationExtension) {
        extensions.configure(JavaApplication::class) {
            mainClass.set(config.fullyQualifiedMainClass)

            afterEvaluate {
                applicationDefaultJvmArgs = config.jvmArgs.get()
            }
        }
    }

    private val JvmApplicationExtension.fullyQualifiedMainClass: Provider<String>
        get() = packageName.zip(mainClass) { packageName, mainClass ->
            if (mainClass.startsWith(".")) {
                "$packageName$mainClass"
            } else {
                mainClass
            }
        }
            .orElse(mainClass)
}
