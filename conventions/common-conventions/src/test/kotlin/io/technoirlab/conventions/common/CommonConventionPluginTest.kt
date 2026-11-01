package io.technoirlab.conventions.common

import io.technoirlab.gradle.test.kit.createRootProject
import io.technoirlab.gradle.test.kit.evaluate
import io.technoirlab.gradle.test.kit.subProject
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.extra
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommonConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = createRootProject("common-fixture") {
            subProject("library1")
            subProject("library2")
        }
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.common")

        project.evaluate()
    }

    @Test
    fun `assigns group and version from project properties`() {
        project.allprojects.forEach { it.setGradleProperties(groupId = "com.example", version = "1.0.0") }
        project.project(":library2").setGradleProperties(groupId = "com.example2", version = "2.0.0")
        project.allprojects.forEach { it.apply(plugin = "io.technoirlab.conventions.common") }

        project.allprojects.forEach { it.evaluate() }

        val coordinates = project.allprojects.associate { it.path to "${it.group}:${it.version}" }
        assertThat(coordinates).containsExactlyInAnyOrderEntriesOf(
            mapOf(
                ":" to "com.example.root:1.0.0",
                ":library1" to "com.example:1.0.0",
                ":library2" to "com.example2:2.0.0",
            ),
        )
    }

    // Gradle exposes gradle.properties entries to each project as extra properties.
    private fun Project.setGradleProperties(groupId: String, version: String) {
        extra["project.groupId"] = groupId
        extra["project.version"] = version
    }
}
