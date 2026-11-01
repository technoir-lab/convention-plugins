package io.technoirlab.conventions.common

import io.technoirlab.gradle.test.kit.GradleRunnerExtension
import io.technoirlab.gradle.test.kit.appendBuildScript
import io.technoirlab.gradle.test.kit.buildScript
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class CommonConventionPluginFunctionalTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("test-project")

    @Test
    fun `dependencies from the same group can be used without explicit version`() {
        gradleRunner.root.project("library1").appendBuildScript(
            """
            dependencies {
                implementation("io.technoirlab.conventions:gradle-extensions")
            }
            """.trimIndent(),
        )

        gradleRunner.build(":library1:jar")
    }

    @Test
    fun `dynamic versions are prohibited`() {
        gradleRunner.root.project("library1").appendBuildScript(
            """
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.+")
            }
            """.trimIndent(),
        )

        val buildResult = gradleRunner.build(":library1:jar", expectFailure = true)

        assertThat(buildResult.output).contains(
            "Could not resolve org.jetbrains.kotlin:kotlin-reflect:2.2.+: Resolution strategy disallows usage of dynamic versions",
        )
    }

    @Test
    fun `sorting dependencies`() {
        val project = gradleRunner.root.project("library1")
            .appendBuildScript(
                """
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.20")
                    api("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")
                }
                """.trimIndent(),
            )

        gradleRunner.build(":library1:sortDependencies")

        assertThat(project.buildScript)
            .content()
            .containsIgnoringNewLines(
                // language=kotlin
                """
                dependencies {
                    api("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")
                
                    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.20")
                }
                """.trimIndent(),
            )
    }
}
