package io.technoirlab.conventions.android

import com.android.build.api.dsl.LibraryExtension
import io.technoirlab.conventions.android.api.AndroidCommonExtension.Companion.DEFAULT_COMPILE_SDK
import io.technoirlab.conventions.android.api.AndroidLibraryExtension
import io.technoirlab.gradle.test.kit.createRootProject
import io.technoirlab.gradle.test.kit.evaluate
import io.technoirlab.gradle.test.kit.subProject
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AndroidLibraryConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        val rootProject = createRootProject("android-library-fixture") {
            subProject("android-library")
        }
        project = rootProject.project(":android-library")
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.android-library")
        project.configure<AndroidLibraryExtension> {
            packageName.set("com.example.android.library")
        }

        project.evaluate()
    }

    @Test
    fun `lint targetSdk defaults to compileSdk`() {
        project.apply(plugin = "io.technoirlab.conventions.android-library")
        project.configure<AndroidLibraryExtension> {
            packageName.set("com.example.android.library")
        }

        project.evaluate()

        assertThat(project.the<LibraryExtension>().lint.targetSdk).isEqualTo(DEFAULT_COMPILE_SDK)
    }

    @Test
    fun `publications use custom Maven coordinates`() {
        project.extra["project.groupId"] = "com.example"
        project.extra["project.version"] = "v1"
        project.apply(plugin = "io.technoirlab.conventions.android-library")
        project.configure<AndroidLibraryExtension> {
            packageName.set("com.example.android.library")
        }
        val publications = project.the<PublishingExtension>().publications.withType<MavenPublication>()

        project.evaluate()

        assertThat(publications.map { "${it.groupId}:${it.artifactId}:${it.version}" }).containsExactly("com.example:android-library:v1")
    }
}
