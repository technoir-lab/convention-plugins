package io.technoirlab.conventions.kotlin.multiplatform

import io.technoirlab.conventions.kotlin.multiplatform.api.KotlinMultiplatformApplicationExtension
import io.technoirlab.gradle.test.kit.evaluate
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KotlinMultiplatformApplicationConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.kotlin-multiplatform-application")
        project.configure<KotlinMultiplatformApplicationExtension> {
            packageName.set("com.example.kmp.application")
        }

        project.evaluate()
    }
}
