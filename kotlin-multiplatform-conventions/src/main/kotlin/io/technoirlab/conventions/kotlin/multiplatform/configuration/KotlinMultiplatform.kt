package io.technoirlab.conventions.kotlin.multiplatform.configuration

import io.technoirlab.conventions.kotlin.multiplatform.BuildConfig
import io.technoirlab.conventions.kotlin.multiplatform.api.KotlinMultiplatformExtension
import io.technoirlab.gradle.capitalized
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.dsl.HasConfigurableKotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinNativeTargetConfigurator.Companion.RUN_GROUP
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.HostManager
import kotlin.io.path.Path
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension as KmpExtension

internal fun Project.configureKotlinMultiplatform(
    config: KotlinMultiplatformExtension,
    kotlinVersion: Provider<KotlinVersion> = provider { KotlinVersion.DEFAULT },
    executable: Boolean = false
) {
    extensions.configure(KmpExtension::class) {
        applyDefaultHierarchyTemplate()

        @OptIn(ExperimentalAbiValidation::class)
        extensions.configure(AbiValidationMultiplatformExtension::class) {
            enabled.set(config.buildFeatures.abiValidation)
        }

        compilerOptions {
            apiVersion.set(kotlinVersion)
            languageVersion.set(kotlinVersion)

            optIn.addAll(
                "kotlin.time.ExperimentalTime",
                "kotlinx.cinterop.ExperimentalForeignApi"
            )
            freeCompilerArgs.addAll(
                "-Xcontext-parameters",
                "-Xconsistent-data-class-copy-visibility",
                "-Xexpect-actual-classes",
            )
        }

        targets.configureEach {
            when (this) {
                is KotlinAndroidTarget -> configureJvmTarget()
                is KotlinJvmTarget -> configureJvmTarget()
                is KotlinNativeTarget -> configureNativeTarget(config.packageName, config.buildFeatures.cinterop, executable)
                is KotlinWasmJsTargetDsl -> configureJsTarget(executable)
            }
        }

        sourceSets {
            commonMain.dependencies {
                implementation(dependencies.platform(BuildConfig.KOTLIN_BOM))
                implementation(dependencies.platform(BuildConfig.KOTLINX_COROUTINES_BOM))
                implementation(dependencies.platform(BuildConfig.KOTLINX_SERIALIZATION_BOM))
            }
        }
    }

    tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).configure {
        if (config.buildFeatures.abiValidation.get()) {
            dependsOn(tasks.named("checkLegacyAbi"))
        }
    }
}

private fun <T> T.configureJvmTarget() where T : KotlinTarget, T : HasConfigurableKotlinCompilerOptions<KotlinJvmCompilerOptions> {
    compilerOptions {
        jvmDefault.set(JvmDefaultMode.NO_COMPATIBILITY)
    }
}

private fun KotlinNativeTarget.configureNativeTarget(
    packageName: Provider<String>,
    enableCInterop: Property<Boolean>,
    executable: Boolean
) {
    if (enableCInterop.get()) {
        compilations.named(KotlinCompilation.MAIN_COMPILATION_NAME) {
            cinterops.register(project.name) {
                packageName(packageName.get())
                val srcPath = Path("src", "nativeInterop", "cinterop")
                compilerOpts("-I$srcPath")
            }
        }
    }

    binaries {
        if (konanTarget.family in setOf(Family.IOS, Family.TVOS, Family.WATCHOS)) {
            framework {
                isStatic = true
                baseName = project.name
            }
        } else if (executable) {
            executable {
                if (packageName.isPresent) {
                    entryPoint = "${packageName.get()}.main"
                }
            }
        }
        if (HostManager.host == konanTarget) {
            withType<Executable>().configureEach {
                val executableName = name
                project.tasks.register("run${executableName.capitalized()}") {
                    group = RUN_GROUP
                    description = "Executes Kotlin/Native executable $executableName for target ${target.name}"
                    dependsOn(runTaskName!!)
                }
            }
        }
    }
}

private fun KotlinWasmJsTargetDsl.configureJsTarget(executable: Boolean) {
    if (executable) {
        binaries.executable()
    }
}
