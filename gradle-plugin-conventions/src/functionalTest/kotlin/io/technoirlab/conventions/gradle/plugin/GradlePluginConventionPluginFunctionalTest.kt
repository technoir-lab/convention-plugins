package io.technoirlab.conventions.gradle.plugin

import io.technoirlab.conventions.common.fixtures.POM_EXPECTED
import io.technoirlab.conventions.common.fixtures.PROJECT_METADATA
import io.technoirlab.gradle.test.kit.Generator
import io.technoirlab.gradle.test.kit.GradleRunnerExtension
import io.technoirlab.gradle.test.kit.appendBuildScript
import io.technoirlab.gradle.test.kit.buildDir
import io.technoirlab.gradle.test.kit.generatedFile
import io.technoirlab.gradle.test.kit.jarEntries
import io.technoirlab.gradle.test.kit.replaceText
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.io.path.createDirectories
import kotlin.io.path.div

class GradlePluginConventionPluginFunctionalTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("gradle-plugin-project")

    @Test
    fun `builds successfully`() {
        gradleRunner.build(":example-plugin:build")
    }

    @Test
    fun `BuildConfig generation`() {
        val project = gradleRunner.root.project("example-plugin")
            .appendBuildScript(
                """
                gradlePluginConfig {
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

        gradleRunner.build(":example-plugin:classes")

        assertThat(project.generatedFile(Generator.BuildConfig, "com.example.plugin.BuildConfig"))
            .content()
            .contains("const val STRING_FIELD: String = \"string value\"")
            .contains("const val LAZY_STRING_FIELD: String = \"Project description\"")
            .contains("val NULLABLE_STRING_FIELD: String? = null")
            .contains("const val BOOLEAN_FIELD: Boolean = true")
            .contains("const val INT_FIELD: Int = 42")
            .doesNotContain("NONEXISTENT_STRING_FIELD")
            .doesNotContain("TEST_STRING_FIELD")

        gradleRunner.build(":example-plugin:testClasses")

        assertThat(project.generatedFile(Generator.BuildConfig, "com.example.plugin.TestBuildConfig", variant = "test"))
            .content()
            .contains("const val TEST_STRING_FIELD: String = \"test string value\"")
            .doesNotContain("INT_FIELD")
    }

    @Test
    fun `unit tests`() {
        val buildResult = gradleRunner.build(":example-plugin:test")

        assertThat(buildResult.task(":example-plugin:test")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `code coverage`() {
        val buildResult = gradleRunner.build(":example-plugin:koverLog")

        assertThat(buildResult.output).contains("application line coverage: 100%")
    }

    @Test
    fun publishing() {
        val repoDir = gradleRunner.root.dir / "repo"
        repoDir.createDirectories()

        gradleRunner.build(":example-plugin:publishAllPublicationsToLocalRepository") {
            gradleProperties += mapOf("publish.local.url" to repoDir.toUri())
        }

        val artifactDir = repoDir / "io/technoirlab/example-plugin/dev"
        assertThat(artifactDir)
            .isDirectoryContaining("glob:**example-plugin-dev.*")
            .isDirectoryContaining("glob:**example-plugin-dev-sources.*")
            .isDirectoryContaining("glob:**example-plugin-dev-javadoc.*")
            .isDirectoryContaining("glob:**example-plugin-dev-api.*")
            .isDirectoryContaining("glob:**example-plugin-dev-api-sources.*")

        val pomFile = artifactDir / "example-plugin-dev.pom"
        assertThat(pomFile)
            .content()
            .contains("<name>example-plugin</name>")

        val sourcesJar = artifactDir / "example-plugin-dev-sources.jar"
        assertThat(sourcesJar).exists()
        assertThat(sourcesJar.jarEntries()).contains("com/example/plugin/ExamplePlugin.kt")

        val javadocJar = artifactDir / "example-plugin-dev-javadoc.jar"
        assertThat(javadocJar).exists()
        assertThat(javadocJar.jarEntries()).contains("com/example/plugin/ExamplePlugin.html")
        assertThat(javadocJar.jarEntries()).contains("com/example/plugin/api/ExampleExtension.html")

        val apiSourcesJar = artifactDir / "example-plugin-dev-api-sources.jar"
        assertThat(apiSourcesJar).exists()
        assertThat(apiSourcesJar.jarEntries()).contains("com/example/plugin/api/ExampleExtension.kt")
    }

    @Test
    fun `publishing with custom coordinates`() {
        val repoDir = gradleRunner.root.dir / "repo"
        repoDir.createDirectories()

        gradleRunner.build(":example-plugin:publishAllPublicationsToLocalRepository") {
            gradleProperties += mapOf(
                "project.groupId" to "com.example",
                "project.version" to "v1",
                "publish.local.url" to repoDir.toUri()
            )
        }

        val artifactDir = repoDir / "com/example/example-plugin/v1"
        assertThat(artifactDir).isDirectoryContaining("glob:**example-plugin-v1*")
    }

    @Test
    fun `publishing with custom metadata`() {
        val repoDir = gradleRunner.root.dir / "repo"
        repoDir.createDirectories()

        gradleRunner.root.project("example-plugin")
            .appendBuildScript("gradlePluginConfig {\n${PROJECT_METADATA.prependIndent("    ")}\n}")

        gradleRunner.build(":example-plugin:publishAllPublicationsToLocalRepository") {
            gradleProperties += mapOf("publish.local.url" to repoDir.toUri())
            environmentVariables += mapOf(
                "GITHUB_SERVER_URL" to "https://github.com",
                "GITHUB_REPOSITORY" to "example-org/example-project",
            )
        }

        val pomFile = repoDir / "io/technoirlab/example-plugin/dev/example-plugin-dev.pom"
        assertThat(pomFile)
            .content()
            .containsIgnoringNewLines(*POM_EXPECTED)
    }

    @Test
    fun `ABI validation`() {
        val project = gradleRunner.root.project("example-plugin")
            .appendBuildScript(
                """
                gradlePluginConfig {
                    buildFeatures {
                        abiValidation = true
                    }
                }
                """.trimIndent()
            )

        gradleRunner.build(":example-plugin:updateLegacyAbi")

        val abiDump = project.dir / "api/example-plugin.api"
        assertThat(abiDump)
            .content()
            .contains(
                """
                public final class com/example/plugin/ExamplePlugin : org/gradle/api/Plugin {
                	public fun <init> ()V
                	public synthetic fun apply (Ljava/lang/Object;)V
                	public fun apply (Lorg/gradle/api/Project;)V
                }
                """.trimIndent()
            )

        (project.dir / "src/main/kotlin/com/example/plugin/ExamplePlugin.kt")
            .replaceText("// function placeholder", "fun hello() = Unit")

        val buildResult = gradleRunner.buildAndFail(":example-plugin:check")

        assertThat(buildResult.task(":example-plugin:checkLegacyAbi")?.outcome).isEqualTo(TaskOutcome.FAILED)
        assertThat(buildResult.output).contains(
            """
            |   	public fun apply (Lorg/gradle/api/Project;)V
            |  +	public final fun hello ()V
            """.trimMargin()
        )
    }

    @Test
    fun `generating documentation`() {
        gradleRunner.build(":example-plugin:dokkaGenerate")

        val project = gradleRunner.root.project("example-plugin")
        assertThat(project.buildDir / "dokka/html/index.html").exists()
        assertThat(project.buildDir / "dokka/html/example-plugin/com.example.plugin/index.html").exists()
        assertThat(project.buildDir / "dokka/html/example-plugin/com.example.plugin.api/index.html").exists()
        assertThat(project.buildDir / "dokka/html/example-plugin/com.example.plugin.internal").doesNotExist()
    }

    @ParameterizedTest
    @CsvSource(
        "8.12,8.14.3,2.0.21,1.9.0,1.7.3",
        "8.14,8.14.3,2.0.21,1.9.0,1.7.3",
        "9.0,9.1.0,2.2.0,1.10.2,1.9.0",
        "9.2,9.2.0,2.2.20,1.10.2,1.9.0",
    )
    fun `Gradle compatibility`(
        minGradleVersion: String,
        testedGradleVersion: String,
        kotlinStdlibVersion: String,
        kotlinCoroutinesVersion: String,
        kotlinSerializationVersion: String,
    ) {
        val repoDir = gradleRunner.root.dir / "repo"

        val project = gradleRunner.root.project("example-plugin")
        project.appendBuildScript("gradlePluginConfig { minGradleVersion = \"$minGradleVersion\" }")

        // Use a Kotlin 2.2 language feature to verify the supported language level
        (project.dir / "src/main/kotlin/com/example/plugin/ExamplePlugin.kt")
            .replaceText("// body placeholder", """$$"multi-dollar string"""")

        gradleRunner.build(":example-plugin:build", ":example-plugin:publishAllPublicationsToLocalRepository") {
            gradleVersion = testedGradleVersion
            gradleProperties += mapOf("publish.local.url" to repoDir.toUri())
        }

        val pomFile = repoDir / "io/technoirlab/example-plugin/dev/example-plugin-dev.pom"
        assertThat(pomFile)
            .content()
            .containsIgnoringNewLines(
                """
                |    <dependency>
                |      <groupId>org.jetbrains.kotlin</groupId>
                |      <artifactId>kotlin-stdlib</artifactId>
                |      <version>$kotlinStdlibVersion</version>
                |      <scope>compile</scope>
                |    </dependency>
                """.trimMargin()
            )
            .containsIgnoringNewLines(
                """
                |      <dependency>
                |        <groupId>org.jetbrains.kotlinx</groupId>
                |        <artifactId>kotlinx-coroutines-bom</artifactId>
                |        <version>$kotlinCoroutinesVersion</version>
                |        <type>pom</type>
                |        <scope>import</scope>
                |      </dependency>
                """.trimMargin()
            )
            .containsIgnoringNewLines(
                """
                |      <dependency>
                |        <groupId>org.jetbrains.kotlinx</groupId>
                |        <artifactId>kotlinx-serialization-bom</artifactId>
                |        <version>$kotlinSerializationVersion</version>
                |        <type>pom</type>
                |        <scope>import</scope>
                |      </dependency>
                """.trimMargin()
            )
    }
}
