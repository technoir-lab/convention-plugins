package io.technoirlab.gradle.test.kit

import org.gradle.api.Project
import org.gradle.api.internal.GradleInternal
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.initialization.SettingsProcessor
import org.gradle.initialization.layout.BuildLayout
import org.gradle.internal.extensions.core.serviceOf
import org.gradle.testfixtures.ProjectBuilder

fun createRootProject(name: String, dsl: Project.() -> Unit = {}): Project = ProjectBuilder.builder()
    .withName(name)
    .build()
    .apply(dsl)
    .also { it.attachSettings() }

fun Project.subProject(name: String, dsl: Project.() -> Unit = {}): Project = ProjectBuilder.builder()
    .withName(name)
    .withParent(this)
    .withProjectDir(projectDir.resolve(name))
    .build()
    .apply(dsl)

fun Project.evaluate() {
    (this as ProjectInternal).evaluate()
}

private fun Project.attachSettings() {
    val gradle = gradle as GradleInternal
    val buildLayout = serviceOf<BuildLayout>()
    val settingsProcessor = serviceOf<SettingsProcessor>()
    val settingsState = settingsProcessor.process(gradle, buildLayout, gradle.classLoaderScope, gradle.startParameter)
    gradle.attachSettings(settingsState)

    rootProject.allprojects.forEach { gradle.projectEvaluationBroadcaster.beforeEvaluate(it) }
}
