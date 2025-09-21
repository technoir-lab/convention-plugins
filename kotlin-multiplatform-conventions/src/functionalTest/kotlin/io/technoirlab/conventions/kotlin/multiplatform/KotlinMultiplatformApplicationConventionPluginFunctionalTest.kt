package io.technoirlab.conventions.kotlin.multiplatform

import io.technoirlab.conventions.common.fixtures.Generator
import io.technoirlab.conventions.common.fixtures.GradleRunnerExtension
import io.technoirlab.conventions.common.fixtures.buildScript
import io.technoirlab.conventions.common.fixtures.configureBuildScript
import io.technoirlab.conventions.common.fixtures.createDependencyGraph
import io.technoirlab.conventions.common.fixtures.generatedFile
import io.technoirlab.conventions.common.fixtures.replaceText
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.moveTo

class KotlinMultiplatformApplicationConventionPluginFunctionalTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("kotlin-multiplatform-project")

    @Test
    fun `builds successfully`() {
        gradleRunner.build(":kmp-application:build")
    }

    @Test
    fun `BuildConfig generation`() {
        val project = gradleRunner.root.project("kmp-application")
            .configureBuildScript(
                """
                    kotlinMultiplatformApplication {
                        buildFeatures {
                            buildConfig {
                                buildConfigField<String>("STRING_FIELD", "string value")
                                buildConfigField<String>("LAZY_STRING_FIELD", provider { project.description })
                                buildConfigField<String>("NONEXISTENT_STRING_FIELD", provider { null })
                                buildConfigField<String>("NULLABLE_STRING_FIELD", null)
                                buildConfigField<Boolean>("BOOLEAN_FIELD", true)
                                buildConfigField<Int>("INT_FIELD", 42)
                                buildConfigField<String>("TEST_STRING_FIELD", "test string value", variant = "test")
                            }
                        }
                    }
                    
                    description = "Project description"
                """.trimIndent()
            )

        gradleRunner.build(":kmp-application:generateBuildConfig")

        assertThat(project.generatedFile(Generator.BuildConfig, "kmp.application.BuildConfig"))
            .content()
            .contains("const val STRING_FIELD: String = \"string value\"")
            .contains("const val LAZY_STRING_FIELD: String = \"Project description\"")
            .contains("val NULLABLE_STRING_FIELD: String? = null")
            .contains("const val BOOLEAN_FIELD: Boolean = true")
            .contains("const val INT_FIELD: Int = 42")
            .doesNotContain("NONEXISTENT_STRING_FIELD")
            .doesNotContain("TEST_STRING_FIELD")

        gradleRunner.build(":kmp-application:generateTestBuildConfig")

        assertThat(project.generatedFile(Generator.BuildConfig, "kmp.application.TestBuildConfig", variant = "test"))
            .content()
            .contains("const val TEST_STRING_FIELD: String = \"test string value\"")
            .doesNotContain("INT_FIELD")
    }

    @Test
    fun `dependency injection`() {
        gradleRunner.root.project("kmp-application")
            .configureBuildScript(
                """
                    kotlinMultiplatformApplication {
                        buildFeatures {
                            metro = true
                        }
                    }
                """.trimIndent()
            )
            .createDependencyGraph()

        gradleRunner.build(":kmp-application:assemble")
    }

    @Test
    fun `default targets`() {
        gradleRunner.root.project("kmp-application")
            .configureBuildScript(
                """
                    kotlinMultiplatformApplication {
                        defaultTargets = true
                    }
                """.trimIndent()
            )

        val buildResult = gradleRunner.build(":kmp-application:tasks")

        assertThat(buildResult.output).contains(
            "androidNativeArm64",
            "iosArm64",
            "iosSimulatorArm64",
            "linuxArm64",
            "linuxX64",
            "macosArm64",
            "mingwX64",
        )
    }

    @Test
    fun `custom package name`() {
        val project = gradleRunner.root.project("kmp-application")
        project.buildScript.replaceText(
            "kotlinMultiplatformApplication {",
            """
                kotlinMultiplatformApplication {
                    packageName = "com.example.kmp.application"
            """.trimIndent()
        )

        val newMainKt = project.dir / "src/commonMain/kotlin/com/example/kmp/application/Main.kt"
        newMainKt.createParentDirectories()
        (project.dir / "src/commonMain/kotlin/kmp/application/Main.kt").moveTo(newMainKt)
        newMainKt.replaceText("package kmp.application", "package com.example.kmp.application")

        val buildResult = gradleRunner.build(":kmp-application:runDebugExecutable")

        assertThat(buildResult.output).contains("Hello, world!")
    }

    @Test
    fun running() {
        val buildResult = gradleRunner.build(":kmp-application:runDebugExecutable")

        assertThat(buildResult.output).contains("Hello, world!")
    }

    @Test
    fun `declaring common dependencies without versions`() {
        gradleRunner.root.project("kmp-application")
            .configureBuildScript(
                """
                    kotlin {
                        sourceSets {
                            commonMain.dependencies {
                                implementation("org.jetbrains.kotlin:kotlin-reflect")
                                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
                                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
                            }
                        }
                    }
                """.trimIndent()
            )

        gradleRunner.build(":kmp-application:assemble")
    }
}
