package io.technoirlab.conventions.android

import io.technoirlab.conventions.android.api.AndroidLibraryExtension
import io.technoirlab.gradle.test.kit.createRootProject
import io.technoirlab.gradle.test.kit.evaluate
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AndroidLibraryConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = createRootProject("android-library-fixture")
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.android-library")
        project.configure<AndroidLibraryExtension> {
            packageName.set("com.example.android.library")
        }

        project.evaluate()
    }
}
