package io.technoirlab.conventions.android

import io.technoirlab.conventions.common.fixtures.POM_EXPECTED
import io.technoirlab.conventions.common.fixtures.PROJECT_METADATA
import io.technoirlab.conventions.common.fixtures.createParcelableClass
import io.technoirlab.conventions.common.fixtures.createSerializableClass
import io.technoirlab.conventions.common.fixtures.createTest
import io.technoirlab.conventions.common.fixtures.createTestFixture
import io.technoirlab.gradle.test.kit.GradleRunnerExtension
import io.technoirlab.gradle.test.kit.appendBuildScript
import io.technoirlab.gradle.test.kit.buildDir
import io.technoirlab.gradle.test.kit.jarEntries
import io.technoirlab.gradle.test.kit.kotlinFile
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.io.path.appendText
import kotlin.io.path.createDirectories
import kotlin.io.path.div

class AndroidLibraryConventionPluginFunctionalTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("android-project")

    @Test
    fun `builds successfully`() {
        gradleRunner.build(":android-library:build")
    }

    @Test
    fun `declaring common dependencies without versions`() {
        gradleRunner.root.project("android-library")
            .appendBuildScript(
                """
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-reflect")
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
                    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
                }
                """.trimIndent(),
            )

        gradleRunner.build(":android-library:assemble")
    }

    @Test
    fun `generating documentation`() {
        gradleRunner.build(":android-library:dokkaGenerate")

        val project = gradleRunner.root.project("android-library")
        assertThat(project.buildDir / "dokka/html/index.html").exists()
        assertThat(project.buildDir / "dokka/html/android-library/com.example.android.library/index.html").exists()
        assertThat(project.buildDir / "dokka/html/android-library/com.example.android.library.internal").doesNotExist()
    }

    @Nested
    inner class CodeGeneration {
        @Test
        fun buildConfig() {
            val project = gradleRunner.root.project("android-library")
                .appendBuildScript(
                    """
                    androidLibrary {
                        buildFeatures {
                            buildConfig {
                                buildConfigField("STRING_FIELD", "string value")
                                buildConfigField("ESCAPED_STRING_FIELD", "\"quoted\" \\ value\n")
                                buildConfigField("LAZY_STRING_FIELD", provider { project.description })
                                buildConfigField("NONEXISTENT_STRING_FIELD", provider<String> { null })
                                buildConfigField("NULLABLE_STRING_FIELD", null as String?)
                                buildConfigField("BOOLEAN_FIELD", true)
                                buildConfigField("INT_FIELD", 42)
                                buildConfigField("LONG_FIELD", 42L)
                                buildConfigField("FLOAT_FIELD", 4.2f)
                                buildConfigField("DOUBLE_FIELD", 4.2)
                                buildConfigField("RELEASE_STRING_FIELD", "release string value", variant = "release")
                            }
                        }
                    }
                    description = "Project description"
                    """.trimIndent(),
                )
            val buildConfigDir = project.buildDir / "generated/source/buildConfig"

            gradleRunner.build(":android-library:compileDebugSources")

            assertThat(buildConfigDir / "debug/com/example/android/library/BuildConfig.java")
                .content()
                .contains("public static final String STRING_FIELD = \"string value\";")
                .contains("""public static final String ESCAPED_STRING_FIELD = "\"quoted\" \\ value\n";""")
                .contains("public static final String LAZY_STRING_FIELD = \"Project description\";")
                .contains("public static final String NULLABLE_STRING_FIELD = null;")
                .contains("public static final boolean BOOLEAN_FIELD = true;")
                .contains("public static final int INT_FIELD = 42;")
                .contains("public static final long LONG_FIELD = 42L;")
                .contains("public static final float FLOAT_FIELD = 4.2f;")
                .contains("public static final double DOUBLE_FIELD = 4.2;")
                .doesNotContain("NONEXISTENT_STRING_FIELD")
                .doesNotContain("RELEASE_STRING_FIELD")

            gradleRunner.build(":android-library:compileReleaseSources")

            assertThat(buildConfigDir / "release/com/example/android/library/BuildConfig.java")
                .content()
                .contains("public static final String STRING_FIELD = \"string value\";")
                .contains("public static final String RELEASE_STRING_FIELD = \"release string value\";")
        }

        @Test
        fun serialization() {
            gradleRunner.root.project("android-library")
                .appendBuildScript(
                    """
                    androidLibrary {
                        buildFeatures {
                            serialization = true
                        }
                    }
                    
                    dependencies {
                        implementation(libs.kotlinx.serialization.core)
                    }
                    """.trimIndent(),
                )
                .createSerializableClass()

            gradleRunner.build(":android-library:compileDebugSources")
        }

        @Test
        fun parcelize() {
            gradleRunner.root.project("android-library")
                .appendBuildScript(
                    """
                    androidLibrary {
                        buildFeatures {
                            parcelize = true
                        }
                    }
                    """.trimIndent(),
                )
                .createParcelableClass()

            gradleRunner.build(":android-library:compileDebugSources")
        }
    }

    @Nested
    inner class Testing {
        @Test
        fun `unit tests`() {
            val buildResult = gradleRunner.build(":android-library:testDebugUnitTest")

            assertThat(buildResult.task(":android-library:testDebugUnitTest")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        @Test
        fun `code coverage`() {
            val buildResult = gradleRunner.build(":android-library:koverLog")

            assertThat(buildResult.output).contains("application line coverage: 100%")
        }

        @Test
        fun `test fixtures`() {
            gradleRunner.root.project("android-library")
                .appendBuildScript(
                    """
                    androidLibrary {
                        buildFeatures {
                            testFixtures = true
                        }
                    }
                    """.trimIndent(),
                )
                .createTestFixture()
                .createTest("""check(TestFixture("fixture").name == "fixture")""")

            val buildResult = gradleRunner.build(":android-library:testDebugUnitTest")

            assertThat(buildResult.task(":android-library:testDebugUnitTest")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    @Nested
    inner class StaticAnalysis {
        @Test
        fun ktLint() {
            val project = gradleRunner.root.project("android-library")
            project.kotlinFile("com.example.android.library.AndroidLibrary").appendText("\nval answer = 42;\n")

            val buildResult = gradleRunner.build(":android-library:ktlintCheck", expectFailure = true)

            assertThat(buildResult.task(":android-library:ktlintMainSourceSetCheck")?.outcome).isEqualTo(TaskOutcome.FAILED)
            assertThat(project.buildDir / "reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.txt").hasContent(
                """
                android-library/src/main/kotlin/com/example/android/library/AndroidLibrary.kt:10:16: Unnecessary semicolon (standard:no-semi)
                
                Summary error count (descending) by rule:
                  standard:no-semi: 1
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

            gradleRunner.build(":android-library:publishAllPublicationsToLocalRepository") {
                gradleProperties += mapOf("publish.local.url" to repoDir.toUri())
            }

            val artifactDir = repoDir / "io/technoirlab/android-library/dev"
            assertThat(artifactDir)
                .isDirectoryContaining("glob:**android-library-dev.aar")
                .isDirectoryContaining("glob:**android-library-dev-javadoc.*")
                .isDirectoryContaining("glob:**android-library-dev-sources.*")
                .isDirectoryNotContaining("glob:**android-library-dev-test-fixtures.*")

            val pomFile = artifactDir / "android-library-dev.pom"
            assertThat(pomFile)
                .content()
                .contains("<name>android-library</name>")

            val sourcesJar = artifactDir / "android-library-dev-sources.jar"
            assertThat(sourcesJar).exists()
            assertThat(sourcesJar.jarEntries()).contains("com/example/android/library/AndroidLibrary.kt")

            val javadocJar = artifactDir / "android-library-dev-javadoc.jar"
            assertThat(javadocJar).exists()
            assertThat(javadocJar.jarEntries()).contains("com/example/android/library/AndroidLibrary.html")
        }

        @Test
        fun `custom Maven coordinates`() {
            val repoDir = gradleRunner.root.dir / "repo"
            repoDir.createDirectories()

            gradleRunner.build(":android-library:publishAllPublicationsToLocalRepository") {
                gradleProperties += mapOf(
                    "project.groupId" to "com.example",
                    "project.version" to "v1",
                    "publish.local.url" to repoDir.toUri(),
                )
            }

            val artifactDir = repoDir / "com/example/android-library/v1"
            assertThat(artifactDir).isDirectoryContaining("glob:**android-library-v1*")
        }

        @Test
        fun `custom metadata`() {
            val repoDir = gradleRunner.root.dir / "repo"
            repoDir.createDirectories()

            gradleRunner.root.project("android-library")
                .appendBuildScript("androidLibrary {\n${PROJECT_METADATA.prependIndent("    ")}\n}")

            gradleRunner.build(":android-library:publishAllPublicationsToLocalRepository") {
                gradleProperties += mapOf("publish.local.url" to repoDir.toUri())
                environmentVariables += mapOf(
                    "ANDROID_HOME" to System.getenv("ANDROID_HOME"),
                    "GITHUB_SERVER_URL" to "https://github.com",
                    "GITHUB_REPOSITORY" to "example-org/example-project",
                )
            }

            val pomFile = repoDir / "io/technoirlab/android-library/dev/android-library-dev.pom"
            assertThat(pomFile)
                .content()
                .containsIgnoringNewLines(*POM_EXPECTED)
        }

        @Test
        fun `test fixtures`() {
            val repoDir = gradleRunner.root.dir / "repo"
            repoDir.createDirectories()

            gradleRunner.root.project("android-library")
                .appendBuildScript(
                    """
                    androidLibrary {
                        buildFeatures {
                            testFixtures = true
                        }
                    }
                    """.trimIndent(),
                )
                .createTestFixture()

            gradleRunner.build(":android-library:publishAllPublicationsToLocalRepository") {
                gradleProperties += mapOf("publish.local.url" to repoDir.toUri())
            }

            val artifactDir = repoDir / "io/technoirlab/android-library/dev"
            assertThat(artifactDir).isDirectoryContaining("glob:**android-library-dev-test-fixtures.aar")
        }
    }
}
