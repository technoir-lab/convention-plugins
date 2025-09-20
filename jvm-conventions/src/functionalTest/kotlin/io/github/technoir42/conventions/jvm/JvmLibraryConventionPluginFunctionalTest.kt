package io.github.technoir42.conventions.jvm

import io.github.technoir42.conventions.common.fixtures.Generator
import io.github.technoir42.conventions.common.fixtures.GradleRunnerExtension
import io.github.technoir42.conventions.common.fixtures.POM_EXPECTED
import io.github.technoir42.conventions.common.fixtures.PROJECT_METADATA
import io.github.technoir42.conventions.common.fixtures.buildDir
import io.github.technoir42.conventions.common.fixtures.configureBuildScript
import io.github.technoir42.conventions.common.fixtures.generatedFile
import io.github.technoir42.conventions.common.fixtures.jarEntries
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.writeText

class JvmLibraryConventionPluginFunctionalTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("jvm-project")

    @Test
    fun `builds successfully`() {
        gradleRunner.build(":jvm-library:build")
    }

    @Test
    fun `BuildConfig generation`() {
        val project = gradleRunner.root.project("jvm-library")
            .configureBuildScript(
                """
                    jvmLibrary {
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

        gradleRunner.build(":jvm-library:classes")

        assertThat(project.generatedFile(Generator.BuildConfig, "com.example.jvm.library.BuildConfig"))
            .content()
            .contains("const val STRING_FIELD: String = \"string value\"")
            .contains("const val LAZY_STRING_FIELD: String = \"Project description\"")
            .contains("val NULLABLE_STRING_FIELD: String? = null")
            .contains("const val BOOLEAN_FIELD: Boolean = true")
            .contains("const val INT_FIELD: Int = 42")
            .doesNotContain("NONEXISTENT_STRING_FIELD")
            .doesNotContain("TEST_STRING_FIELD")

        gradleRunner.build(":jvm-library:testClasses")

        assertThat(project.generatedFile(Generator.BuildConfig, "com.example.jvm.library.TestBuildConfig", variant = "test"))
            .content()
            .contains("const val TEST_STRING_FIELD: String = \"test string value\"")
            .doesNotContain("INT_FIELD")
    }

    @Test
    fun publishing() {
        val repoDir = gradleRunner.root.dir / "repo"
        repoDir.createDirectories()

        gradleRunner.build(":jvm-library:publish") {
            gradleProperties += mapOf("publish.url" to repoDir.toUri())
        }

        val artifactDir = repoDir / "io/github/technoir42/jvm-library/dev"
        assertThat(artifactDir)
            .isDirectoryContaining("glob:**jvm-library-dev.*")
            .isDirectoryContaining("glob:**jvm-library-dev-javadoc.*")
            .isDirectoryContaining("glob:**jvm-library-dev-sources.*")

        val pomFile = artifactDir / "jvm-library-dev.pom"
        assertThat(pomFile)
            .content()
            .contains("<name>jvm-library</name>")

        val sourcesJar = artifactDir / "jvm-library-dev-sources.jar"
        assertThat(sourcesJar).exists()
        assertThat(sourcesJar.jarEntries()).contains("com/example/jvm/library/JvmLibrary.kt")

        val javadocJar = artifactDir / "jvm-library-dev-javadoc.jar"
        assertThat(javadocJar).exists()
        assertThat(javadocJar.jarEntries()).contains("com/example/jvm/library/JvmLibrary.html")
    }

    @Test
    fun `publishing with custom Maven coordinates`() {
        val repoDir = gradleRunner.root.dir / "repo"
        repoDir.createDirectories()

        gradleRunner.build(":jvm-library:publish") {
            gradleProperties += mapOf(
                "project.groupId" to "com.example",
                "project.version" to "v1",
                "publish.url" to repoDir.toUri()
            )
        }

        val artifactDir = repoDir / "com/example/jvm-library/v1"
        assertThat(artifactDir).isDirectoryContaining("glob:**jvm-library-v1*")
    }

    @Test
    fun `publishing with custom metadata`() {
        val repoDir = gradleRunner.root.dir / "repo"
        repoDir.createDirectories()

        gradleRunner.root.project("jvm-library")
            .configureBuildScript("jvmLibrary {\n${PROJECT_METADATA.prependIndent("    ")}\n}")

        gradleRunner.build(":jvm-library:publish") {
            gradleProperties += mapOf("publish.url" to repoDir.toUri())
            environmentVariables += mapOf(
                "GITHUB_SERVER_URL" to "https://github.com",
                "GITHUB_REPOSITORY" to "example-org/example-project",
            )
        }

        val pomFile = repoDir / "io/github/technoir42/jvm-library/dev/jvm-library-dev.pom"
        assertThat(pomFile)
            .content()
            .containsIgnoringNewLines(*POM_EXPECTED)
    }

    @Test
    fun `declaring common dependencies without versions`() {
        gradleRunner.root.project("jvm-library")
            .configureBuildScript(
                """
                    dependencies {
                        implementation("org.jetbrains.kotlin:kotlin-reflect")
                        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
                        implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
                    }
                """.trimIndent()
            )

        gradleRunner.build(":jvm-library:assemble")
    }

    @Test
    fun `ABI validation`() {
        val project = gradleRunner.root.project("jvm-library")
            .configureBuildScript(
                """
                    jvmLibrary {
                        buildFeatures {
                            abiValidation = true
                        }
                    }
                """.trimIndent()
            )

        gradleRunner.build(":jvm-library:updateLegacyAbi")

        val abiDump = project.dir / "api/jvm-library.api"
        assertThat(abiDump)
            .content()
            .contains(
                """
                    public final class com/example/jvm/library/JvmLibrary {
                    	public fun <init> ()V
                    }
                """.trimIndent()
            )

        (project.dir / "src/main/kotlin/com/example/jvm/library/JvmLibrary.kt")
            .writeText(
                """
                    package com.example.jvm.library
                    
                    class JvmLibrary {
                        fun hello() = Unit
                    }
                    
                """.trimIndent()
            )

        val buildResult = gradleRunner.buildAndFail(":jvm-library:check")

        assertThat(buildResult.task(":jvm-library:checkLegacyAbi")?.outcome).isEqualTo(TaskOutcome.FAILED)
        assertThat(buildResult.output).contains(
            """
                |   public final class com/example/jvm/library/JvmLibrary {
                |   	public fun <init> ()V
                |  +	public final fun hello ()V
                |   }
            """.trimMargin()
        )
    }

    @Test
    fun `generating documentation`() {
        gradleRunner.build(":jvm-library:dokkaGenerate")

        val project = gradleRunner.root.project("jvm-library")
        assertThat(project.buildDir / "dokka/html/index.html").exists()
        assertThat(project.buildDir / "dokka/html/jvm-library/com.example.jvm.library/index.html").exists()
    }
}
