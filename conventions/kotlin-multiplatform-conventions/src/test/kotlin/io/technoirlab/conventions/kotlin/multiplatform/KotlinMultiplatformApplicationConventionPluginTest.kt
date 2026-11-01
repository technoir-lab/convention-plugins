package io.technoirlab.conventions.kotlin.multiplatform

import io.technoirlab.conventions.kotlin.multiplatform.api.KotlinMultiplatformApplicationExtension
import io.technoirlab.gradle.test.kit.createRootProject
import io.technoirlab.gradle.test.kit.evaluate
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KotlinMultiplatformApplicationConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = createRootProject("kmp-application-fixture")
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.kotlin-multiplatform-application")
        project.configure<KotlinMultiplatformApplicationExtension> {
            packageName.set("com.example.kmp.application")
        }

        project.evaluate()
    }

    @Test
    fun `custom package name sets executable entry points`() {
        project.apply(plugin = "io.technoirlab.conventions.kotlin-multiplatform-application")
        project.configure<KotlinMultiplatformApplicationExtension> {
            packageName.set("com.example.kmp.application")
        }
        val binaries = project.the<KotlinMultiplatformExtension>().linuxX64().binaries

        project.evaluate()

        assertThat(binaries.getExecutable(NativeBuildType.DEBUG).entryPoint).isEqualTo("com.example.kmp.application.main")
        assertThat(binaries.getExecutable(NativeBuildType.RELEASE).entryPoint).isEqualTo("com.example.kmp.application.main")
    }

    @Test
    fun `registers default binaries for native targets`() {
        project.apply(plugin = "io.technoirlab.conventions.kotlin-multiplatform-application")
        project.configure<KotlinMultiplatformExtension> {
            androidNativeArm64()
            androidNativeX64()
            iosArm64()
            linuxX64()
            macosArm64()
            mingwX64()
            tvosArm64()
            watchosArm64()
        }

        project.evaluate()

        assertThat(project.nativeBinaryNames()).containsExactlyInAnyOrderEntriesOf(
            mapOf(
                "androidNativeArm64" to setOf("debugShared", "releaseShared"),
                "androidNativeX64" to setOf("debugShared", "releaseShared"),
                "iosArm64" to setOf("staticDebugFramework", "staticReleaseFramework"),
                "linuxX64" to setOf("debugExecutable", "releaseExecutable"),
                "macosArm64" to setOf("debugExecutable", "releaseExecutable", "staticDebugFramework", "staticReleaseFramework"),
                "mingwX64" to setOf("debugExecutable", "releaseExecutable"),
                "tvosArm64" to setOf("staticDebugFramework", "staticReleaseFramework"),
                "watchosArm64" to setOf("staticDebugFramework", "staticReleaseFramework"),
            ),
        )
    }

    @Test
    fun `debug binaries enable assertions`() {
        project.apply(plugin = "io.technoirlab.conventions.kotlin-multiplatform-application")
        val binaries = project.the<KotlinMultiplatformExtension>().linuxX64().binaries

        project.evaluate()

        assertThat(binaries.getExecutable(NativeBuildType.DEBUG).freeCompilerArgs).contains("-ea")
        assertThat(binaries.getExecutable(NativeBuildType.RELEASE).freeCompilerArgs).doesNotContain("-ea")
    }

    private fun Project.nativeBinaryNames(): Map<String, Set<String>> =
        the<KotlinMultiplatformExtension>().targets.withType<KotlinNativeTarget>().associate { target ->
            val binaryNames = target.binaries.filterNot { it is TestExecutable }.map { it.name }.toSet()
            target.name to binaryNames
        }
}
