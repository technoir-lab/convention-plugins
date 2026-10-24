package io.technoirlab.conventions.android

import io.technoirlab.conventions.common.fixtures.createParcelableClass
import io.technoirlab.conventions.common.fixtures.createSerializableClass
import io.technoirlab.gradle.test.kit.GradleRunnerExtension
import io.technoirlab.gradle.test.kit.appendBuildScript
import io.technoirlab.gradle.test.kit.buildDir
import io.technoirlab.gradle.test.kit.kotlinFile
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.io.path.appendText
import kotlin.io.path.div

class AndroidApplicationConventionPluginFunctionalTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("android-project")

    @Test
    fun `builds successfully`() {
        gradleRunner.build(":android-application:build")
    }

    @Test
    fun packaging() {
        gradleRunner.build(":android-application:assembleDebug")

        val outputDir = gradleRunner.root.project("android-application").buildDir / "outputs/apk/debug"
        assertThat(outputDir / "android-application-debug.apk").exists()
        assertThat(outputDir / "output-metadata.json")
            .content()
            .contains("\"applicationId\": \"com.example.android.application\"")
            .contains("\"versionCode\": 1")
            .contains("\"versionName\": \"dev\"")
            .contains("\"minSdkVersionForDexing\": 26")
    }

    @Test
    fun `custom version`() {
        val project = gradleRunner.root.project("android-application")
            .appendBuildScript(
                """
                androidApplication {
                    versionCode = 42
                    versionName = "1.2.3"
                }
                """.trimIndent(),
            )

        gradleRunner.build(":android-application:assembleDebug")

        assertThat(project.buildDir / "outputs/apk/debug/output-metadata.json")
            .content()
            .contains("\"versionCode\": 42")
            .contains("\"versionName\": \"1.2.3\"")
    }

    @Test
    fun `declaring common dependencies without versions`() {
        gradleRunner.root.project("android-application").appendBuildScript(
            """
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-reflect")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
            }
            """.trimIndent(),
        )

        gradleRunner.build(":android-application:assemble")
    }

    @Nested
    inner class CodeGeneration {
        @Test
        fun buildConfig() {
            val project = gradleRunner.root.project("android-application")
                .appendBuildScript(
                    """
                    androidApplication {
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
                                buildConfigField("DEBUG_STRING_FIELD", "debug string value", variant = "debug")
                            }
                        }
                    }
                    description = "Project description"
                    """.trimIndent(),
                )
            val buildConfigDir = project.buildDir / "generated/source/buildConfig"

            gradleRunner.build(":android-application:compileDebugSources")

            assertThat(buildConfigDir / "debug/com/example/android/application/BuildConfig.java")
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
                .contains("public static final String DEBUG_STRING_FIELD = \"debug string value\";")
                .doesNotContain("NONEXISTENT_STRING_FIELD")

            gradleRunner.build(":android-application:compileReleaseSources")

            assertThat(buildConfigDir / "release/com/example/android/application/BuildConfig.java")
                .content()
                .contains("public static final String STRING_FIELD = \"string value\";")
                .doesNotContain("DEBUG_STRING_FIELD")
        }

        @Test
        fun serialization() {
            gradleRunner.root.project("android-application")
                .appendBuildScript(
                    """
                    androidApplication {
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

            gradleRunner.build(":android-application:compileDebugSources")
        }

        @Test
        fun parcelize() {
            gradleRunner.root.project("android-application")
                .appendBuildScript(
                    """
                    androidApplication {
                        buildFeatures {
                            parcelize = true
                        }
                    }
                    """.trimIndent(),
                )
                .createParcelableClass()

            gradleRunner.build(":android-application:compileDebugSources")
        }
    }

    @Nested
    inner class Testing {
        @Test
        fun `unit tests`() {
            val buildResult = gradleRunner.build(":android-application:testDebugUnitTest")

            assertThat(buildResult.task(":android-application:testDebugUnitTest")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        @Test
        fun `code coverage`() {
            val buildResult = gradleRunner.build(":android-application:koverLog")

            assertThat(buildResult.output).contains("application line coverage: 100%")
        }
    }

    @Nested
    inner class StaticAnalysis {
        @Test
        fun ktLint() {
            val project = gradleRunner.root.project("android-application")
            project.kotlinFile("com.example.android.application.Greeting").appendText("\nval answer = 42;\n")

            val buildResult = gradleRunner.build(":android-application:ktlintCheck", expectFailure = true)

            assertThat(buildResult.task(":android-application:ktlintMainSourceSetCheck")?.outcome).isEqualTo(TaskOutcome.FAILED)
            assertThat(project.buildDir / "reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.txt").hasContent(
                """
                android-application/src/main/kotlin/com/example/android/application/Greeting.kt:5:16: Unnecessary semicolon (standard:no-semi)
                
                Summary error count (descending) by rule:
                  standard:no-semi: 1
                """.trimIndent(),
            )
        }
    }
}
