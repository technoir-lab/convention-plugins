package io.technoirlab.conventions.jvm

import io.technoirlab.conventions.jvm.api.JvmLibraryExtension
import io.technoirlab.gradle.test.kit.createRootProject
import io.technoirlab.gradle.test.kit.evaluate
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JvmLibraryConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = createRootProject("jvm-library-fixture")
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.jvm-library")
        project.configure<JvmLibraryExtension> {
            packageName.set("com.example.jvm.library")
        }

        project.evaluate()
    }
}
