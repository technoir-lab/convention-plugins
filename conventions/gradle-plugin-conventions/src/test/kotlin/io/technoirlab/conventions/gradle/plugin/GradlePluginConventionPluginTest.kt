package io.technoirlab.conventions.gradle.plugin

import io.technoirlab.conventions.gradle.plugin.api.GradlePluginExtension
import io.technoirlab.gradle.test.kit.createRootProject
import io.technoirlab.gradle.test.kit.evaluate
import io.technoirlab.gradle.test.kit.subProject
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GradlePluginConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        val rootProject = createRootProject("gradle-plugin-fixture") {
            subProject("example-plugin")
        }
        project = rootProject.project(":example-plugin")
    }

    @Test
    fun `applies to a project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.gradle-plugin")
        project.configure<GradlePluginExtension> {
            packageName.set("com.example.gradle.plugin")
        }

        project.evaluate()
    }

    @Test
    fun `main JAR manifest contains project implementation version`() {
        project.extra["project.version"] = "v1"
        project.apply(plugin = "io.technoirlab.conventions.gradle-plugin")
        project.configure<GradlePluginExtension> {
            packageName.set("com.example.gradle.plugin")
        }

        project.evaluate()

        val implementationVersion = project.tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME).get().manifest.attributes["Implementation-Version"]
        assertThat(implementationVersion).isInstanceOfSatisfying(Provider::class.java) { provider ->
            assertThat(provider.get()).isEqualTo("v1")
        }
    }

    @Test
    fun `publications use custom Maven coordinates`() {
        project.extra["project.groupId"] = "com.example"
        project.extra["project.version"] = "v1"
        project.apply(plugin = "io.technoirlab.conventions.gradle-plugin")
        project.configure<GradlePluginExtension> {
            packageName.set("com.example.gradle.plugin")
        }
        val publications = project.the<PublishingExtension>().publications.withType<MavenPublication>()

        project.evaluate()

        assertThat(publications.map { "${it.groupId}:${it.artifactId}:${it.version}" }).containsExactly("com.example:example-plugin:v1")
    }
}
