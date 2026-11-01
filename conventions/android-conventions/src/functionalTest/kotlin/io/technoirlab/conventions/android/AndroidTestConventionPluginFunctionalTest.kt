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

class AndroidTestConventionPluginFunctionalTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("android-project")

    @Test
    fun `builds successfully`() {
        gradleRunner.build(":android-test:build")
    }

    @Test
    fun packaging() {
        gradleRunner.build(":android-test:assembleDebug")

        val project = gradleRunner.root.project("android-test")
        val outputDir = project.buildDir / "outputs/apk/debug"
        assertThat(outputDir / "android-test-debug.apk").exists()
        assertThat(outputDir / "output-metadata.json")
            .content()
            .contains("\"applicationId\": \"com.example.android.test\"")
            .contains("\"minSdkVersionForDexing\": 26")
        assertThat(project.buildDir / PACKAGED_MANIFEST_PATH)
            .content()
            .contains("android:targetPackage=\"com.example.android.application\"")
            .contains("android:minSdkVersion=\"26\"")
            .contains("android:targetSdkVersion=\"37\"")
    }

    @Test
    fun `custom test application ID and target SDK`() {
        val project = gradleRunner.root.project("android-test")
            .appendBuildScript(
                """
                androidTest {
                    testApplicationId = "com.example.android.application.test"
                    targetSdk = 36
                }
                """.trimIndent(),
            )

        gradleRunner.build(":android-test:assembleDebug")

        assertThat(project.buildDir / "outputs/apk/debug/output-metadata.json")
            .content()
            .contains("\"applicationId\": \"com.example.android.application.test\"")
        assertThat(project.buildDir / PACKAGED_MANIFEST_PATH)
            .content()
            .contains("android:targetSdkVersion=\"36\"")
    }

    @Test
    fun `declaring common dependencies without versions`() {
        gradleRunner.root.project("android-test").appendBuildScript(
            """
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-reflect")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
            }
            """.trimIndent(),
        )

        gradleRunner.build(":android-test:assemble")
    }

    @Nested
    inner class CodeGeneration {
        @Test
        fun buildConfig() {
            val project = gradleRunner.root.project("android-test")
                .appendBuildScript(
                    """
                    androidTest {
                        buildFeatures {
                            buildConfig {
                                buildConfigField("STRING_FIELD", "string value")
                                buildConfigField("DEBUG_STRING_FIELD", "debug string value", variant = "debug")
                            }
                        }
                    }
                    """.trimIndent(),
                )

            gradleRunner.build(":android-test:compileDebugSources")

            assertThat(project.buildDir / "generated/source/buildConfig/debug/com/example/android/test/BuildConfig.java")
                .content()
                .contains("public static final String STRING_FIELD = \"string value\";")
                .contains("public static final String DEBUG_STRING_FIELD = \"debug string value\";")
        }

        @Test
        fun serialization() {
            gradleRunner.root.project("android-test")
                .appendBuildScript(
                    """
                    androidTest {
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

            gradleRunner.build(":android-test:compileDebugSources")
        }

        @Test
        fun parcelize() {
            gradleRunner.root.project("android-test")
                .appendBuildScript(
                    """
                    androidTest {
                        buildFeatures {
                            parcelize = true
                        }
                    }
                    """.trimIndent(),
                )
                .createParcelableClass()

            gradleRunner.build(":android-test:compileDebugSources")
        }
    }

    @Nested
    inner class StaticAnalysis {
        @Test
        fun ktLint() {
            val project = gradleRunner.root.project("android-test")
            project.kotlinFile("com.example.android.test.GreetingTest").appendText("\nval answer = 42;\n")

            val buildResult = gradleRunner.build(":android-test:ktlintCheck", expectFailure = true)

            assertThat(buildResult.task(":android-test:ktlintMainSourceSetCheck")?.outcome).isEqualTo(TaskOutcome.FAILED)
            assertThat(project.buildDir / "reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.txt").hasContent(
                """
                android-test/src/main/kotlin/com/example/android/test/GreetingTest.kt:14:16: Unnecessary semicolon (standard:no-semi)
                
                Summary error count (descending) by rule:
                  standard:no-semi: 1
                """.trimIndent(),
            )
        }
    }

    private companion object {
        private const val PACKAGED_MANIFEST_PATH = "intermediates/packaged_manifests/debug/processDebugManifest/AndroidManifest.xml"
    }
}
