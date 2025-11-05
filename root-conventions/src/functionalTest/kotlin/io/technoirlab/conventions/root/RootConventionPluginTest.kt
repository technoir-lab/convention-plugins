package io.technoirlab.conventions.root

import io.technoirlab.gradle.test.kit.GradleRunnerExtension
import io.technoirlab.gradle.test.kit.appendBuildScript
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class RootConventionPluginTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("sample-project")

    @Test
    fun building() {
        gradleRunner.build(":build")
    }

    @Test
    fun cleanup() {
        gradleRunner.build(":clean")
    }

    @Test
    fun `coverage aggregation`() {
        gradleRunner.root.appendBuildScript(
            """
            dependencies {
                kover(project(":jvm-library"))
            }
            """.trimIndent()
        )

        val buildResult = gradleRunner.build(":koverLog")

        assertThat(buildResult.output).contains("application line coverage: 100%")
    }

    @Test
    fun `dependency sorting`() {
        gradleRunner.build(":sortDependencies")
    }

    @Test
    fun `documentation aggregation`() {
        gradleRunner.root.appendBuildScript(
            """
            dependencies {
                dokka(project(":jvm-library"))
            }
            """.trimIndent()
        )

        val buildResult = gradleRunner.build(":dokkaGenerate")

        assertThat(buildResult.task(":jvm-library:dokkaGenerateModuleHtml")?.outcome).isNotNull
    }
}
