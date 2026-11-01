package io.technoirlab.conventions.kotlin.multiplatform

import io.technoirlab.conventions.common.fixtures.POM_EXPECTED
import io.technoirlab.conventions.common.fixtures.PROJECT_METADATA
import io.technoirlab.conventions.common.fixtures.createBenchmark
import io.technoirlab.conventions.common.fixtures.createDependencyGraph
import io.technoirlab.conventions.common.fixtures.createRedactedFixtures
import io.technoirlab.conventions.common.fixtures.createSerializableClass
import io.technoirlab.gradle.test.kit.Generator
import io.technoirlab.gradle.test.kit.GradleRunnerExtension
import io.technoirlab.gradle.test.kit.appendBuildScript
import io.technoirlab.gradle.test.kit.buildDir
import io.technoirlab.gradle.test.kit.buildScript
import io.technoirlab.gradle.test.kit.generatedFile
import io.technoirlab.gradle.test.kit.jarEntries
import io.technoirlab.gradle.test.kit.kotlinFile
import io.technoirlab.gradle.test.kit.replaceText
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.nio.file.FileSystems
import kotlin.io.path.appendText
import kotlin.io.path.createDirectories
import kotlin.io.path.div

class KotlinMultiplatformLibraryConventionPluginFunctionalTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("kotlin-multiplatform-project")

    @Test
    fun `builds successfully`() {
        gradleRunner.build(":kmp-library:build")
    }

    @Test
    fun `custom package name`() {
        val project = gradleRunner.root.project("kmp-library")
        project.buildScript.replaceText(
            "kotlinMultiplatformLibrary {",
            """
            kotlinMultiplatformLibrary {
                packageName = "com.example.kmp.library"
            """.trimIndent(),
        )

        (project.dir / "src/nativeMain/kotlin/kmp/library/Greet.kt")
            .replaceText("nativeGreet(", "com.example.kmp.library.nativeGreet(")

        gradleRunner.build(":kmp-library:assemble")
    }

    @Test
    fun `declaring common dependencies without versions`() {
        gradleRunner.root.project("kmp-library").appendBuildScript(
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

        gradleRunner.build(":kmp-library:assemble")
    }

    @Test
    fun `generating documentation`() {
        gradleRunner.build(":kmp-library:dokkaGenerate")

        val project = gradleRunner.root.project("kmp-library")
        assertThat(project.buildDir / "dokka/html/index.html").exists()
        assertThat(project.buildDir / "dokka/html/kmp-library/kmp.library/index.html").exists()
        assertThat(project.buildDir / "dokka/html/kmp-library/kmp.library.internal").doesNotExist()
    }

    @Nested
    inner class CodeGeneration {
        @Test
        fun buildConfig() {
            val project = gradleRunner.root.project("kmp-library")
                .appendBuildScript(
                    """
                    kotlinMultiplatformLibrary {
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

            gradleRunner.build(":kmp-library:generateBuildConfig")

            assertThat(project.generatedFile(Generator.BuildConfig, "kmp.library.BuildConfig"))
                .content()
                .contains("const val STRING_FIELD: String = \"string value\"")
                .contains("const val LAZY_STRING_FIELD: String = \"Project description\"")
                .contains("val NULLABLE_STRING_FIELD: String? = null")
                .contains("const val BOOLEAN_FIELD: Boolean = true")
                .contains("const val INT_FIELD: Int = 42")
                .doesNotContain("NONEXISTENT_STRING_FIELD")
                .doesNotContain("TEST_STRING_FIELD")

            gradleRunner.build(":kmp-library:generateTestBuildConfig")

            assertThat(project.generatedFile(Generator.BuildConfig, "kmp.library.TestBuildConfig", variant = "test"))
                .content()
                .contains("const val TEST_STRING_FIELD: String = \"test string value\"")
                .doesNotContain("INT_FIELD")
        }

        @Test
        fun `dependency injection`() {
            gradleRunner.root.project("kmp-library")
                .appendBuildScript(
                    """
                    kotlinMultiplatformLibrary {
                        buildFeatures {
                            metro = true
                        }
                    }
                    """.trimIndent(),
                )
                .createDependencyGraph()

            gradleRunner.build(":kmp-library:assemble")
        }

        @Test
        fun serialization() {
            gradleRunner.root.project("kmp-library")
                .appendBuildScript(
                    """
                    kotlinMultiplatformLibrary {
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

            gradleRunner.build(":kmp-library:assemble")
        }

        @Test
        fun `toString() redaction`() {
            gradleRunner.root.project("kmp-library")
                .appendBuildScript(
                    """
                    kotlinMultiplatformLibrary {
                        buildFeatures {
                            redacted = true
                        }
                    }
                    """.trimIndent(),
                )
                .createRedactedFixtures(variant = "commonMain", testVariant = "commonTest")

            gradleRunner.build(":kmp-library:jvmTest")
        }
    }

    @Nested
    inner class Targets {
        @Test
        fun `Android target`() {
            val project = gradleRunner.root.project("kmp-library")

            val buildResult = gradleRunner.build(":kmp-library:assembleAndroidMain")

            assertThat(buildResult.task(":kmp-library:compileAndroidMain")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
            FileSystems.newFileSystem(project.buildDir / "outputs/aar/kmp-library.aar").use { aar ->
                assertThat(aar.getPath("AndroidManifest.xml"))
                    .content()
                    .contains("package=\"kmp.library\"")
                    .contains("android:minSdkVersion=\"26\"")
            }
        }
    }

    @Nested
    inner class Testing {
        @Test
        fun `unit tests`() {
            val buildResult = gradleRunner.build(":kmp-library:jvmTest")

            assertThat(buildResult.task(":kmp-library:jvmTest")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        @Test
        fun `code coverage`() {
            val buildResult = gradleRunner.build(":kmp-library:koverLog")

            assertThat(buildResult.output).contains("application line coverage: 100%")
        }

        @Test
        fun benchmarking() {
            gradleRunner.root.project("kmp-library")
                .appendBuildScript(
                    """
                    kotlinMultiplatformLibrary {
                        buildFeatures {
                            benchmark = true
                        }
                    }
                    """.trimIndent(),
                )
                .createBenchmark()

            val buildResult = gradleRunner.build(":kmp-library:jvmBenchmarkBenchmark")

            assertThat(buildResult.task(":kmp-library:jvmBenchmarkBenchmark")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    @Nested
    inner class StaticAnalysis {
        @Test
        fun ktLint() {
            val project = gradleRunner.root.project("kmp-library")
            project.kotlinFile("kmp.library.KmpLibrary", variant = "commonMain").appendText("\nval answer = 42;\n")

            val buildResult = gradleRunner.build(":kmp-library:ktlintCheck", expectFailure = true)

            assertThat(buildResult.task(":kmp-library:ktlintCommonMainSourceSetCheck")?.outcome).isEqualTo(TaskOutcome.FAILED)
            assertThat(project.buildDir / "reports/ktlint/ktlintCommonMainSourceSetCheck/ktlintCommonMainSourceSetCheck.txt").hasContent(
                """
                kmp-library/src/commonMain/kotlin/kmp/library/KmpLibrary.kt:7:16: Unnecessary semicolon (standard:no-semi)
                
                Summary error count (descending) by rule:
                  standard:no-semi: 1
                """.trimIndent(),
            )
        }

        @Test
        fun `ABI validation`() {
            val project = gradleRunner.root.project("kmp-library")
                .appendBuildScript(
                    """
                    kotlinMultiplatformLibrary {
                        buildFeatures {
                            abiValidation = true
                        }
                    }
                    """.trimIndent(),
                )

            gradleRunner.build(":kmp-library:updateKotlinAbi")

            val abiDump = project.dir / "api/kmp-library.klib.api"
            assertThat(abiDump)
                .content()
                .contains(
                    """
                    // Library unique name: <io.technoirlab:kmp-library>
                    abstract interface kmp.library/KmpLibrary { // kmp.library/KmpLibrary|null[0]
                        abstract fun hello(kotlin/String) // kmp.library/KmpLibrary.hello|hello(kotlin.String){}[0]
                    }
                    """.trimIndent(),
                )

            (project.dir / "src/commonMain/kotlin/kmp/library/internal/KmpLibraryImpl.kt")
                .replaceText("// function placeholder", "fun hello() = Unit")

            val buildResult = gradleRunner.build(":kmp-library:check", expectFailure = true)

            assertThat(buildResult.task(":kmp-library:checkKotlinAbi")?.outcome).isEqualTo(TaskOutcome.FAILED)
            assertThat(buildResult.output).contains(
                """
                +    final fun hello() // kmp.library.internal/KmpLibraryImpl.hello|hello(){}[0]
                """.trimIndent(),
            )
        }
    }

    @Nested
    inner class Publishing {
        @Test
        fun artifacts() {
            val repoDir = gradleRunner.root.dir / "repo"
            repoDir.createDirectories()

            gradleRunner.build(":kmp-library:publishAllPublicationsToLocalRepository") {
                gradleProperties += mapOf("publish.local.url" to repoDir.toUri())
            }

            val artifactDir = repoDir / "io/technoirlab/kmp-library/dev"
            assertThat(artifactDir)
                .isDirectoryContaining("glob:**kmp-library-dev.*")
                .isDirectoryContaining("glob:**kmp-library-dev-sources.*")

            val pomFile = artifactDir / "kmp-library-dev.pom"
            assertThat(pomFile)
                .content()
                .contains("<name>kmp-library</name>")

            val sourcesJar = artifactDir / "kmp-library-dev-sources.jar"
            assertThat(sourcesJar).exists()
            assertThat(sourcesJar.jarEntries()).contains("commonMain/kmp/library/KmpLibrary.kt")

            val javadocJar = artifactDir / "kmp-library-dev-html-docs.jar"
            assertThat(javadocJar).exists()
            assertThat(javadocJar.jarEntries()).contains("kmp-library/kmp.library/index.html")
        }

        @Test
        fun `custom Maven coordinates`() {
            val repoDir = gradleRunner.root.dir / "repo"
            repoDir.createDirectories()

            gradleRunner.build(":kmp-library:publishAllPublicationsToLocalRepository") {
                gradleProperties += mapOf(
                    "project.groupId" to "com.example.kmp",
                    "project.version" to "v1",
                    "publish.local.url" to repoDir.toUri(),
                )
            }

            val artifactDir = repoDir / "com/example/kmp/kmp-library/v1"
            assertThat(artifactDir).isDirectoryContaining("glob:**kmp-library-v1*")
        }

        @Test
        fun `custom metadata`() {
            val repoDir = gradleRunner.root.dir / "repo"
            repoDir.createDirectories()

            gradleRunner.root.project("kmp-library")
                .appendBuildScript("kotlinMultiplatformLibrary {\n${PROJECT_METADATA.prependIndent("    ")}\n}")

            gradleRunner.build(":kmp-library:publishAllPublicationsToLocalRepository") {
                gradleProperties += mapOf("publish.local.url" to repoDir.toUri())
                environmentVariables += mapOf(
                    "ANDROID_HOME" to System.getenv("ANDROID_HOME"),
                    "GITHUB_SERVER_URL" to "https://github.com",
                    "GITHUB_REPOSITORY" to "example-org/example-project",
                )
            }

            val pomFile = repoDir / "io/technoirlab/kmp-library/dev/kmp-library-dev.pom"
            assertThat(pomFile)
                .content()
                .containsIgnoringNewLines(*POM_EXPECTED)

            val targetPomFile = repoDir / "io/technoirlab/kmp-library-linuxx64/dev/kmp-library-linuxx64-dev.pom"
            assertThat(targetPomFile)
                .content()
                .containsIgnoringNewLines(*POM_EXPECTED)
        }
    }
}
