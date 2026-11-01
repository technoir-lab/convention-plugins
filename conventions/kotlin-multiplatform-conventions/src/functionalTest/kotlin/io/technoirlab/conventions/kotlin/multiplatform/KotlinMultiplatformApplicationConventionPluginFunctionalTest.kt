package io.technoirlab.conventions.kotlin.multiplatform

import io.technoirlab.conventions.common.fixtures.createDependencyGraph
import io.technoirlab.conventions.common.fixtures.createRedactedFixtures
import io.technoirlab.conventions.common.fixtures.createSerializableClass
import io.technoirlab.gradle.test.kit.Generator
import io.technoirlab.gradle.test.kit.GradleRunnerExtension
import io.technoirlab.gradle.test.kit.appendBuildScript
import io.technoirlab.gradle.test.kit.buildDir
import io.technoirlab.gradle.test.kit.generatedFile
import io.technoirlab.gradle.test.kit.kotlinFile
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.nio.file.FileSystems
import kotlin.io.path.appendText
import kotlin.io.path.div

class KotlinMultiplatformApplicationConventionPluginFunctionalTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("kotlin-multiplatform-project")

    @Test
    fun `builds successfully`() {
        gradleRunner.build(":kmp-application:build")
    }

    @Test
    fun `running native`() {
        val buildResult = gradleRunner.build(":kmp-application:runDebugExecutable")

        assertThat(buildResult.output).contains("Hello, world!")
    }

    @Test
    fun `declaring common dependencies without versions`() {
        gradleRunner.root.project("kmp-application")
            .appendBuildScript(
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
                """.trimIndent(),
            )

        gradleRunner.build(":kmp-application:assemble")
    }

    @Nested
    inner class CodeGeneration {
        @Test
        fun buildConfig() {
            val project = gradleRunner.root.project("kmp-application")
                .appendBuildScript(
                    """
                    kotlinMultiplatformApplication {
                        buildFeatures {
                            buildConfig {
                                buildConfigField("STRING_FIELD", "string value")
                                buildConfigField("LAZY_STRING_FIELD", provider { project.description })
                                buildConfigField("NONEXISTENT_STRING_FIELD", provider<String> { null })
                                buildConfigField("NULLABLE_STRING_FIELD", null as String?)
                                buildConfigField("BOOLEAN_FIELD", true)
                                buildConfigField("INT_FIELD", 42)
                                buildConfigField("TEST_STRING_FIELD", "test string value", variant = "test")
                            }
                        }
                    }
                    description = "Project description"
                    """.trimIndent(),
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
                .appendBuildScript(
                    """
                    kotlinMultiplatformApplication {
                        buildFeatures {
                            metro = true
                        }
                    }
                    """.trimIndent(),
                )
                .createDependencyGraph()

            gradleRunner.build(":kmp-application:assemble")
        }

        @Test
        fun serialization() {
            gradleRunner.root.project("kmp-application")
                .appendBuildScript(
                    """
                    kotlinMultiplatformApplication {
                        buildFeatures {
                            serialization = true
                        }
                    }
                    
                    kotlin {
                        sourceSets {
                            commonMain.dependencies {
                                implementation(libs.kotlinx.serialization.core)
                            }
                        }
                    }
                    """.trimIndent(),
                )
                .createSerializableClass(variant = "commonMain")

            gradleRunner.build(":kmp-application:assemble")
        }

        @Test
        fun `toString() redaction`() {
            gradleRunner.root.project("kmp-application")
                .appendBuildScript(
                    """
                    kotlinMultiplatformApplication {
                        buildFeatures {
                            redacted = true
                        }
                    }
                    """.trimIndent(),
                )
                .createRedactedFixtures(variant = "commonMain", testVariant = "commonTest")

            gradleRunner.build(":kmp-application:jvmTest")
        }
    }

    @Nested
    inner class Targets {
        @Test
        fun `Android target`() {
            val project = gradleRunner.root.project("kmp-application")

            val buildResult = gradleRunner.build(":kmp-application:assembleAndroidMain")

            assertThat(buildResult.task(":kmp-application:compileAndroidMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            FileSystems.newFileSystem(project.buildDir / "outputs/aar/kmp-application.aar").use { aar ->
                assertThat(aar.getPath("AndroidManifest.xml"))
                    .content()
                    .contains("package=\"kmp.application\"")
                    .contains("android:minSdkVersion=\"26\"")
            }
        }
    }

    @Nested
    inner class Testing {
        @Test
        fun `unit tests`() {
            val buildResult = gradleRunner.build(":kmp-application:jvmTest")

            assertThat(buildResult.task(":kmp-application:jvmTest")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        @Test
        fun `code coverage`() {
            val buildResult = gradleRunner.build(":kmp-application:koverLog")

            assertThat(buildResult.output).contains("application line coverage: 100%")
        }
    }

    @Nested
    inner class StaticAnalysis {
        @Test
        fun ktLint() {
            val project = gradleRunner.root.project("kmp-application")
            project.kotlinFile("kmp.application.Main", variant = "commonMain").appendText("\nval answer = 42;\n")

            val buildResult = gradleRunner.build(":kmp-application:ktlintCheck", expectFailure = true)

            assertThat(buildResult.task(":kmp-application:ktlintCommonMainSourceSetCheck")?.outcome).isEqualTo(TaskOutcome.FAILED)
            assertThat(project.buildDir / "reports/ktlint/ktlintCommonMainSourceSetCheck/ktlintCommonMainSourceSetCheck.txt").hasContent(
                """
                kmp-application/src/commonMain/kotlin/kmp/application/Main.kt:7:16: Unnecessary semicolon (standard:no-semi)
                
                Summary error count (descending) by rule:
                  standard:no-semi: 1
                """.trimIndent(),
            )
        }
    }
}
