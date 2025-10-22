package io.technoirlab.conventions.settings

import io.technoirlab.gradle.test.kit.GradleRunnerExtension
import io.technoirlab.gradle.test.kit.appendBuildScript
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class SettingsConventionPluginFunctionalTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("sample-project")

    @Test
    fun `dependency analysis`() {
        gradleRunner.build(":buildHealth")
    }

    @Test
    fun `dynamic versions are prohibited`() {
        gradleRunner.root.project("jvm-library").appendBuildScript(
            """
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.+")
            }
            """.trimIndent()
        )

        val buildResult = gradleRunner.buildAndFail(":jvm-library:jar")

        assertThat(buildResult.output).contains(
            "Could not resolve org.jetbrains.kotlin:kotlin-reflect:2.2.+: Resolution strategy disallows usage of dynamic versions"
        )
    }

    @Test
    fun `per-project repositories are prohibited`() {
        gradleRunner.root.appendBuildScript(
            """
            repositories {
                mavenCentral()
            }
            """.trimIndent()
        )

        val buildResult = gradleRunner.buildAndFail(":help")

        assertThat(buildResult.output).contains(
            "Build was configured to prefer settings repositories over project repositories but " +
                "repository 'MavenRepo' was added by build file 'build.gradle.kts'"
        )
    }
}
