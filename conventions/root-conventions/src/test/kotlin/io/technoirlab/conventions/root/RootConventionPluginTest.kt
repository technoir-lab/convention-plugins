package io.technoirlab.conventions.root

import io.technoirlab.gradle.test.kit.evaluate
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RootConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    fun `applies to the root project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.root")

        project.evaluate()
    }
}
