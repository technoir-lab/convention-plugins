package io.technoirlab.conventions.root.configuration

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask

internal fun Project.configureDokka() {
    pluginManager.apply("org.jetbrains.dokka")

    tasks.named(LifecycleBasePlugin.BUILD_TASK_NAME) {
        dependsOn(tasks.withType(DokkaGeneratePublicationTask::class))
    }
}
