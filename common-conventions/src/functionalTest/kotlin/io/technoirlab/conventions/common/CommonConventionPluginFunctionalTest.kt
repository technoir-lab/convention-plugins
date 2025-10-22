package io.technoirlab.conventions.common

import io.technoirlab.gradle.test.kit.GradleRunnerExtension
import io.technoirlab.gradle.test.kit.appendBuildScript
import io.technoirlab.gradle.test.kit.buildScript
import io.technoirlab.gradle.test.kit.gradleProperties
import io.technoirlab.gradle.test.kit.settingsScript
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.io.path.appendText
import kotlin.io.path.writeText

class CommonConventionPluginFunctionalTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("test-project")

    @Test
    fun `group and version assignment`() {
        gradleRunner.root.gradleProperties.appendText(
            // language=properties
            """
            project.groupId=com.example
            project.version=1.0.0
            """.trimIndent()
        )
        gradleRunner.settingsScript.appendText(
            // language=kotlin
            $$"""
            gradle.lifecycle.afterProject {
                logger.lifecycle("Path: '$path', group: '$group', version: '$version'")
            }
            """.trimIndent()
        )

        gradleRunner.root.project("library2").gradleProperties
            .writeText(
                // language=properties
                """
                project.groupId=com.example2
                project.version=2.0.0
                """.trimIndent()
            )

        val buildResult = gradleRunner.build("help")

        assertThat(buildResult.output)
            .contains("Path: ':', group: 'com.example.root', version: '1.0.0'")
            .contains("Path: ':library1', group: 'com.example', version: '1.0.0'")
            .contains("Path: ':library2', group: 'com.example2', version: '2.0.0'")
    }

    @Test
    fun `dependencies from the same group can be used without explicit version`() {
        gradleRunner.root.project("library1").appendBuildScript(
            """
            dependencies {
                implementation("io.technoirlab.conventions:gradle-extensions")
            }
            """.trimIndent()
        )

        gradleRunner.build(":library1:jar")
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
                """.trimIndent()
            )

        gradleRunner.build(":library1:sortDependencies")

        assertThat(project.buildScript)
            .content()
            .contains(
                // language=kotlin
                """
                dependencies {
                    api("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")
                
                    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.20")
                }
                """.trimIndent()
            )
    }
}
