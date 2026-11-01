package io.technoirlab.conventions.root

import io.technoirlab.gradle.test.kit.createRootProject
import io.technoirlab.gradle.test.kit.evaluate
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.io.path.createFile

class RootConventionPluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = createRootProject("root-fixture")
    }

    @Test
    fun `applies to the root project successfully`() {
        project.apply(plugin = "io.technoirlab.conventions.root")

        project.evaluate()
    }

    @Test
    fun `registers the clean task`() {
        project.apply(plugin = "io.technoirlab.conventions.root")

        project.evaluate()

        assertThat(project.tasks.names).contains(LifecycleBasePlugin.CLEAN_TASK_NAME)
    }

    @Test
    fun `registers the dependency sorting task`() {
        // The sort-dependencies plugin registers its tasks only for projects that have a build file.
        project.buildFile.toPath().createFile()
        project.apply(plugin = "io.technoirlab.conventions.root")

        project.evaluate()

        assertThat(project.tasks.names).contains("sortDependencies")
    }
}
