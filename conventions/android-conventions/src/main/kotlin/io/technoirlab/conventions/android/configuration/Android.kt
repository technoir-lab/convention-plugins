@file:Suppress("UnusedReceiverParameter")

package io.technoirlab.conventions.android.configuration

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.TestExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.api.variant.TestAndroidComponentsExtension
import io.technoirlab.conventions.android.BuildConfig
import io.technoirlab.conventions.android.api.AndroidApplicationExtension
import io.technoirlab.conventions.android.api.AndroidCommonExtension
import io.technoirlab.conventions.android.api.AndroidLibraryExtension
import io.technoirlab.conventions.android.api.AndroidTestExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureAndroidApplication(config: AndroidApplicationExtension, finaliseDsl: (ApplicationExtension) -> Unit) {
    val androidExtension = extensions.getByType<ApplicationExtension>()
    val androidComponents = extensions.getByType<ApplicationAndroidComponentsExtension>()
    configureAndroidCommon(androidExtension, config)

    androidComponents.finalizeDsl { android ->
        configureAndroidCommonAfterEvaluate(android, config)
        configureAndroidApplicationAfterEvaluate(android, config)
        finaliseDsl(android)
    }
}

internal fun Project.configureAndroidLibrary(config: AndroidLibraryExtension, finaliseDsl: (LibraryExtension) -> Unit) {
    val androidExtension = extensions.getByType<LibraryExtension>()
    val androidComponents = extensions.getByType<LibraryAndroidComponentsExtension>()
    configureAndroidCommon(androidExtension, config)

    androidComponents.finalizeDsl { android ->
        configureAndroidCommonAfterEvaluate(android, config)
        configureAndroidLibraryAfterEvaluate(android, config)
        finaliseDsl(android)
    }
}

internal fun Project.configureAndroidTest(config: AndroidTestExtension, finaliseDsl: (TestExtension) -> Unit) {
    val androidExtension = extensions.getByType<TestExtension>()
    val androidComponents = extensions.getByType<TestAndroidComponentsExtension>()
    configureAndroidCommon(androidExtension, config)

    androidComponents.finalizeDsl { android ->
        configureAndroidCommonAfterEvaluate(android, config)
        configureAndroidTestAfterEvaluate(android, config)
        finaliseDsl(android)
    }
}

private fun Project.configureAndroidCommon(androidExtension: CommonExtension, config: AndroidCommonExtension) = with(androidExtension) {
    compileOptions.apply {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        "coreLibraryDesugaring"(BuildConfig.DESUGAR_JDK_LIBS)
    }
}

private fun Project.configureAndroidCommonAfterEvaluate(androidExtension: CommonExtension, config: AndroidCommonExtension) =
    with(androidExtension) {
        compileSdk = config.compileSdk.get()
        namespace = config.packageName.get()

        defaultConfig.apply {
            minSdk = config.minSdk.get()
        }
    }

private fun Project.configureAndroidApplicationAfterEvaluate(androidExtension: ApplicationExtension, config: AndroidApplicationExtension) =
    with(androidExtension) {
        defaultConfig {
            applicationId = config.applicationId.get()
            targetSdk = config.targetSdk.get()
            versionCode = config.versionCode.get()
            versionName = config.versionName.get()
        }
    }

private fun Project.configureAndroidLibraryAfterEvaluate(androidExtension: LibraryExtension, config: AndroidLibraryExtension) =
    with(androidExtension) {
        @Suppress("UnstableApiUsage")
        testFixtures.enable = config.buildFeatures.testFixtures.get()

        publishing.singleVariant(RELEASE_VARIANT) {
            withSourcesJar()
        }
    }

private fun Project.configureAndroidTestAfterEvaluate(androidExtension: TestExtension, config: AndroidTestExtension) =
    with(androidExtension) {
        defaultConfig {
            testApplicationId = config.testApplicationId.get()
            targetSdk = config.targetSdk.get()
        }

        targetProjectPath = config.targetProjectPath.get()
    }

internal const val RELEASE_VARIANT = "release"
