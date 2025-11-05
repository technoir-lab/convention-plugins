package io.technoirlab.conventions.jvm

import io.technoirlab.gradle.test.kit.Generator
import io.technoirlab.gradle.test.kit.GradleRunnerExtension
import io.technoirlab.gradle.test.kit.appendBuildScript
import io.technoirlab.gradle.test.kit.generatedFile
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class JvmApplicationConventionPluginFunctionalTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("jvm-project")

    @Test
    fun `builds successfully`() {
        gradleRunner.build(":jvm-application:build")
    }

    @Test
    fun `BuildConfig generation`() {
        val project = gradleRunner.root.project("jvm-application")
            .appendBuildScript(
                """
                jvmApplication {
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
                """.trimIndent()
            )

        gradleRunner.build(":jvm-application:classes")

        assertThat(project.generatedFile(Generator.BuildConfig, "com.example.jvm.application.BuildConfig"))
            .content()
            .contains("const val STRING_FIELD: String = \"string value\"")
            .contains("const val LAZY_STRING_FIELD: String = \"Project description\"")
            .contains("val NULLABLE_STRING_FIELD: String? = null")
            .contains("const val BOOLEAN_FIELD: Boolean = true")
            .contains("const val INT_FIELD: Int = 42")
            .doesNotContain("NONEXISTENT_STRING_FIELD")
            .doesNotContain("TEST_STRING_FIELD")

        gradleRunner.build(":jvm-application:testClasses")

        assertThat(project.generatedFile(Generator.BuildConfig, "com.example.jvm.application.TestBuildConfig", variant = "test"))
            .content()
            .contains("const val TEST_STRING_FIELD: String = \"test string value\"")
            .doesNotContain("INT_FIELD")
    }

    @Test
    fun `unit tests`() {
        val buildResult = gradleRunner.build(":jvm-application:test")

        assertThat(buildResult.task(":jvm-application:test")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `code coverage`() {
        val buildResult = gradleRunner.build(":jvm-application:koverLog")

        assertThat(buildResult.output).contains("application line coverage: 100%")
    }

    @Test
    fun running() {
        val buildResult = gradleRunner.build(":jvm-application:run")

        assertThat(buildResult.output).contains("Hello, world!")
    }

    @Test
    fun `declaring common dependencies without versions`() {
        gradleRunner.root.project("jvm-application").appendBuildScript(
            """
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-reflect")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
            }
            """.trimIndent()
        )

        gradleRunner.build(":jvm-application:assemble")
    }
}
