package io.technoirlab.conventions.android

import io.technoirlab.conventions.android.api.AndroidTestExtension
import io.technoirlab.gradle.test.kit.createRootProject
import io.technoirlab.gradle.test.kit.evaluate
import io.technoirlab.gradle.test.kit.subProject
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AndroidTestConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        val rootProject = createRootProject("android-test-fixture") {
            subProject("android-application")
            subProject("android-test")
        }
        project = rootProject.project(":android-test")
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.android-test")
        project.configure<AndroidTestExtension> {
            packageName.set("com.example.android.test")
            targetProjectPath.set(":android-application")
        }

        project.evaluate()
    }

    @Test
    fun `fails evaluation when target project path is missing`() {
        project.apply(plugin = "io.technoirlab.conventions.android-test")
        project.configure<AndroidTestExtension> {
            packageName.set("com.example.android.test")
        }

        assertThatThrownBy { project.evaluate() }
            .isInstanceOf(ProjectConfigurationException::class.java)
            .hasRootCauseMessage(
                "Cannot query the value of extension 'androidTest' property 'targetProjectPath' because it has no value available.",
            )
    }

    @Test
    fun `targetSdk defaults to compileSdk`() {
        project.apply(plugin = "io.technoirlab.conventions.android-test")
        val extension = project.the<AndroidTestExtension>()
        extension.compileSdk.set(36)

        val targetSdk = extension.targetSdk.get()

        assertThat(targetSdk).isEqualTo(36)
    }
}
