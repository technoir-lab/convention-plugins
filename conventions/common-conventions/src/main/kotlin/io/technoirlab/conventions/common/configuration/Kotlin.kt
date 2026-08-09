package io.technoirlab.conventions.common.configuration

import io.technoirlab.conventions.common.KotlinDiagnostics
import io.technoirlab.gradle.dependencies.implementation
import org.gradle.api.HasImplicitReceiver
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.abi.BinariesSource
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.samWithReceiver.gradle.SamWithReceiverExtension

fun Project.configureKotlin(
    kotlinConfig: Provider<KotlinConfig> = provider { KotlinConfig.DEFAULT },
    enableAbiValidation: Provider<Boolean> = provider { false },
) {
    when (val kotlinExtension = extensions.getByType<KotlinProjectExtension>()) {
        is KotlinAndroidProjectExtension -> kotlinExtension.compilerOptions.configure(kotlinConfig)
        is KotlinJvmProjectExtension -> kotlinExtension.compilerOptions.configure(kotlinConfig)
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.plugin.sam.with.receiver") {
        configure<SamWithReceiverExtension> {
            annotation(checkNotNull(HasImplicitReceiver::class.qualifiedName))
        }
    }

    afterEvaluate {
        configure<KotlinProjectExtension> {
            coreLibrariesVersion = kotlinConfig.get().coreLibrariesVersion

            if (enableAbiValidation.get()) {
                @OptIn(ExperimentalAbiValidation::class)
                abiValidation {
                    binariesSource.set(BinariesSource.NON_TEST_COMPILATIONS)
                }
            }
        }
    }

    val kotlinLibraries = kotlinConfig.map { KotlinLibraries(it.coreLibrariesVersion) }
    dependencies {
        implementation(kotlinLibraries.map { platform(it.kotlinBom) })
        implementation(kotlinLibraries.map { platform(it.kotlinCoroutinesBom) })
        implementation(kotlinLibraries.map { platform(it.kotlinSerializationBom) })
    }

    configureTestFixtures(kotlinLibraries)
}

internal fun KotlinJvmCompilerOptions.configure(kotlinConfig: Provider<KotlinConfig>) {
    (this as KotlinCommonCompilerOptions).configure(kotlinConfig)
    jvmDefault.set(JvmDefaultMode.NO_COMPATIBILITY)
}

fun KotlinCommonCompilerOptions.configure(kotlinConfig: Provider<KotlinConfig>) {
    apiVersion.set(kotlinConfig.map { it.apiVersion })
    languageVersion.set(kotlinConfig.map { it.languageVersion })
    extraWarnings.set(true)
    freeCompilerArgs.addAll(
        "-Xconsistent-data-class-copy-visibility",
        "-Xreturn-value-checker=check",
        "-Xwarning-level=${KotlinDiagnostics.NOTHING_TO_INLINE}:disabled",
    )
}
