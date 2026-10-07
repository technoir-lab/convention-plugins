package io.technoirlab.conventions.jvm

import io.technoirlab.conventions.jvm.api.JvmApplicationExtension
import io.technoirlab.gradle.test.kit.evaluate
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JvmApplicationConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.jvm-application")
        project.configure<JvmApplicationExtension> {
            packageName.set("com.example.jvm.application")
            mainClass.set(".MainKt")
        }

        project.evaluate()
    }
}
