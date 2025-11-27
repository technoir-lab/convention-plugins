package io.technoirlab.conventions.jvm

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
import kotlin.io.path.createDirectories
import kotlin.io.path.div

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
            .appendBuildScript(
                """
                jvmLibrary {
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
    fun `unit tests`() {
        val buildResult = gradleRunner.build(":jvm-library:test")

        assertThat(buildResult.task(":jvm-library:test")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `code coverage`() {
        val buildResult = gradleRunner.build(":jvm-library:koverLog")

        assertThat(buildResult.output).contains("application line coverage: 100%")
    }

    @Test
    fun publishing() {
        val repoDir = gradleRunner.root.dir / "repo"
        repoDir.createDirectories()

        gradleRunner.build(":jvm-library:publishAllPublicationsToLocalRepository") {
            gradleProperties += mapOf("publish.local.url" to repoDir.toUri())
        }

        val artifactDir = repoDir / "io/technoirlab/jvm-library/dev"
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

        gradleRunner.build(":jvm-library:publishAllPublicationsToLocalRepository") {
            gradleProperties += mapOf(
                "project.groupId" to "com.example",
                "project.version" to "v1",
                "publish.local.url" to repoDir.toUri()
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
            .appendBuildScript("jvmLibrary {\n${PROJECT_METADATA.prependIndent("    ")}\n}")

        gradleRunner.build(":jvm-library:publishAllPublicationsToLocalRepository") {
            gradleProperties += mapOf("publish.local.url" to repoDir.toUri())
            environmentVariables += mapOf(
                "GITHUB_SERVER_URL" to "https://github.com",
                "GITHUB_REPOSITORY" to "example-org/example-project",
            )
        }

        val pomFile = repoDir / "io/technoirlab/jvm-library/dev/jvm-library-dev.pom"
        assertThat(pomFile)
            .content()
            .containsIgnoringNewLines(*POM_EXPECTED)
    }

    @Test
    fun `declaring common dependencies without versions`() {
        gradleRunner.root.project("jvm-library")
            .appendBuildScript(
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
            .appendBuildScript(
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
                public abstract interface class com/example/jvm/library/JvmLibrary {
                	public abstract fun hello (Ljava/lang/String;)V
                }
                """.trimIndent()
            )

        (project.dir / "src/main/kotlin/com/example/jvm/library/internal/JvmLibraryImpl.kt")
            .replaceText("// function placeholder", "fun hello() = Unit")

        val buildResult = gradleRunner.build(":jvm-library:check", expectFailure = true)

        assertThat(buildResult.task(":jvm-library:checkLegacyAbi")?.outcome).isEqualTo(TaskOutcome.FAILED)
        assertThat(buildResult.output).contains(
            """
            +	public final fun hello ()V
            """.trimIndent()
        )
    }

    @Test
    fun `generating documentation`() {
        gradleRunner.build(":jvm-library:dokkaGenerate")

        val project = gradleRunner.root.project("jvm-library")
        assertThat(project.buildDir / "dokka/html/index.html").exists()
        assertThat(project.buildDir / "dokka/html/jvm-library/com.example.jvm.library/index.html").exists()
        assertThat(project.buildDir / "dokka/html/jvm-library/com.example.jvm.library.internal").doesNotExist()
    }
}
