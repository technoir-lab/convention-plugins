package io.technoirlab.conventions.gradle.plugin

import io.technoirlab.conventions.gradle.plugin.api.GradlePluginExtension
import io.technoirlab.gradle.test.kit.evaluate
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GradlePluginConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.gradle-plugin")
        project.configure<GradlePluginExtension> {
            packageName.set("com.example.gradle.plugin")
        }

        project.evaluate()
    }
}
