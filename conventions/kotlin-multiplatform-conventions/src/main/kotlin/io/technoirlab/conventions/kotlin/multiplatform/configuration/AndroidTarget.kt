package io.technoirlab.conventions.kotlin.multiplatform.configuration

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import io.technoirlab.conventions.android.api.AndroidCommonExtension.Companion.DEFAULT_COMPILE_SDK
import io.technoirlab.conventions.android.api.AndroidCommonExtension.Companion.DEFAULT_MIN_SDK
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureAndroidTarget(packageName: Provider<String>) {
    configure<KotlinMultiplatformExtension> {
        targets.withType<KotlinMultiplatformAndroidLibraryTarget>().configureEach {
            configureJvmTarget()
        }
    }

    configure<KotlinMultiplatformAndroidComponentsExtension> {
        finalizeDsl { android ->
            android.namespace = packageName.get()
            android.applySdkDefaults()
        }
    }
}

private fun KotlinMultiplatformAndroidLibraryExtension.applySdkDefaults() {
    if (compileSdk == null && compileSdkPreview == null) {
        compileSdk = DEFAULT_COMPILE_SDK
    }
    if (minSdk == null && minSdkPreview == null) {
        minSdk = DEFAULT_MIN_SDK
    }
    if (lint.targetSdk == null && lint.targetSdkPreview == null) {
        lint.targetSdk = compileSdk
    }
}
