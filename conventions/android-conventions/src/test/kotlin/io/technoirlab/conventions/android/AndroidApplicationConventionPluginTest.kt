package io.technoirlab.conventions.android

import io.technoirlab.conventions.android.api.AndroidApplicationExtension
import io.technoirlab.gradle.test.kit.createRootProject
import io.technoirlab.gradle.test.kit.evaluate
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AndroidApplicationConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = createRootProject("android-application-fixture")
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.android-application")
        project.configure<AndroidApplicationExtension> {
            applicationId.set("com.example.android.application")
            packageName.set("com.example.android.application")
        }

        project.evaluate()
    }
}
