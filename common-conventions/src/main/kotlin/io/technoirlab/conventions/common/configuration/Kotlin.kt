package io.technoirlab.conventions.common.configuration

import io.technoirlab.conventions.common.BuildConfig
import io.technoirlab.conventions.common.internal.StandardLibraries
import io.technoirlab.gradle.dependencies.implementation
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

fun Project.configureKotlin(
    kotlinVersion: Provider<KotlinVersion> = provider { KotlinVersion.DEFAULT },
    stdlibVersion: Provider<String> = provider { BuildConfig.KOTLIN_VERSION },
    enableAbiValidation: Provider<Boolean>
) {
    extensions.configure(KotlinJvmProjectExtension::class) {
        compilerOptions {
            apiVersion.set(kotlinVersion)
            languageVersion.set(kotlinVersion)
            jvmDefault.set(JvmDefaultMode.NO_COMPATIBILITY)
            optIn.addAll(
                "kotlin.io.path.ExperimentalPathApi",
                "kotlin.time.ExperimentalTime"
            )
            freeCompilerArgs.addAll(
                "-Xcontext-parameters",
                "-Xconsistent-data-class-copy-visibility",
                "-Xnested-type-aliases",
            )
        }

        @OptIn(ExperimentalAbiValidation::class)
        extensions.configure(AbiValidationExtension::class) {
            enabled.set(enableAbiValidation)
        }
    }

    dependencies {
        val standardLibraries = stdlibVersion.map { StandardLibraries(it) }
        implementation(standardLibraries.map { platform(it.kotlinBom) })
        implementation(standardLibraries.map { platform(it.kotlinCoroutinesBom) })
        implementation(standardLibraries.map { platform(it.kotlinSerializationBom) })
    }

    tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).configure {
        if (enableAbiValidation.get()) {
            dependsOn(tasks.named("checkLegacyAbi"))
        }
    }

    afterEvaluate {
        extensions.configure(KotlinJvmExtension::class) {
            compilerOptions {
                if (stdlibVersion.isPresent) {
                    coreLibrariesVersion = stdlibVersion.get()
                }
            }
        }
    }
}
