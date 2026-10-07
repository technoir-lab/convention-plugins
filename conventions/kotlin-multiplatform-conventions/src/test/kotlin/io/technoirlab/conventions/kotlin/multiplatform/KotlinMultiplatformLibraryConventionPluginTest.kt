package io.technoirlab.conventions.kotlin.multiplatform

import io.technoirlab.conventions.kotlin.multiplatform.api.KotlinMultiplatformLibraryExtension
import io.technoirlab.gradle.test.kit.createRootProject
import io.technoirlab.gradle.test.kit.evaluate
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KotlinMultiplatformLibraryConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = createRootProject("kmp-library-fixture")
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.kotlin-multiplatform-library")
        project.configure<KotlinMultiplatformLibraryExtension> {
            packageName.set("com.example.kmp.library")
        }

        project.evaluate()
    }
}
