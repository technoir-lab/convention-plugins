package io.technoirlab.conventions.kotlin.multiplatform

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import io.technoirlab.conventions.android.api.AndroidCommonExtension.Companion.DEFAULT_COMPILE_SDK
import io.technoirlab.conventions.android.api.AndroidCommonExtension.Companion.DEFAULT_MIN_SDK
import io.technoirlab.conventions.kotlin.multiplatform.api.KotlinMultiplatformLibraryExtension
import io.technoirlab.gradle.test.kit.createRootProject
import io.technoirlab.gradle.test.kit.evaluate
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KotlinMultiplatformLibraryConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = createRootProject("kmp-library-fixture")
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.kotlin-multiplatform-library")
        project.configure<KotlinMultiplatformLibraryExtension> {
            packageName.set("com.example.kmp.library")
        }

        project.evaluate()
    }

    @Test
    fun `Android target defaults`() {
        project.apply(plugin = "io.technoirlab.conventions.kotlin-multiplatform-library")
        project.apply(plugin = "com.android.kotlin.multiplatform.library")
        project.configure<KotlinMultiplatformLibraryExtension> {
            packageName.set("com.example.kmp.library")
        }
        val androidTarget = project.androidTarget()

        project.evaluate()

        assertThat(androidTarget.namespace).isEqualTo("com.example.kmp.library")
        assertThat(androidTarget.compileSdk).isEqualTo(DEFAULT_COMPILE_SDK)
        assertThat(androidTarget.minSdk).isEqualTo(DEFAULT_MIN_SDK)
        assertThat(androidTarget.lint.targetSdk).isEqualTo(DEFAULT_COMPILE_SDK)
    }

    @Test
    fun `explicit Android SDK levels are preserved`() {
        project.apply(plugin = "io.technoirlab.conventions.kotlin-multiplatform-library")
        project.apply(plugin = "com.android.kotlin.multiplatform.library")
        val androidTarget = project.androidTarget().apply {
            compileSdk = 36
            minSdk = 30
            lint.targetSdk = 35
        }

        project.evaluate()

        assertThat(androidTarget.compileSdk).isEqualTo(36)
        assertThat(androidTarget.minSdk).isEqualTo(30)
        assertThat(androidTarget.lint.targetSdk).isEqualTo(35)
    }

    private fun Project.androidTarget(): KotlinMultiplatformAndroidLibraryTarget =
        the<KotlinMultiplatformExtension>().targets.withType<KotlinMultiplatformAndroidLibraryTarget>().single()
}
