package io.technoirlab.conventions.jvm

import io.technoirlab.conventions.jvm.api.JvmLibraryExtension
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

class JvmLibraryConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        val rootProject = createRootProject("jvm-library-fixture") {
            subProject("jvm-library")
        }
        project = rootProject.project(":jvm-library")
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.jvm-library")
        project.configure<JvmLibraryExtension> {
            packageName.set("com.example.jvm.library")
        }

        project.evaluate()
    }

    @Test
    fun `publications use custom Maven coordinates`() {
        project.extra["project.groupId"] = "com.example"
        project.extra["project.version"] = "v1"
        project.apply(plugin = "io.technoirlab.conventions.jvm-library")
        project.configure<JvmLibraryExtension> {
            packageName.set("com.example.jvm.library")
        }
        val publications = project.the<PublishingExtension>().publications.withType<MavenPublication>()

        project.evaluate()

        assertThat(publications.map { "${it.groupId}:${it.artifactId}:${it.version}" }).containsExactly("com.example:jvm-library:v1")
    }
}
