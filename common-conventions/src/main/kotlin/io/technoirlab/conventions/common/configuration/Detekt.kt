package io.technoirlab.conventions.common.configuration

import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.tasks.SourceTask
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin

fun Project.configureDetekt() {
    pluginManager.apply("dev.detekt")

    extensions.configure(DetektExtension::class) {
        config.from(layout.settingsDirectory.dir("config").file("detekt.yaml"))
        buildUponDefaultConfig.set(true)
    }

    val detektConfig = the<DetektExtension>()
    configurations.configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == DETEKT_GROUP_ID && requested.version.isNullOrEmpty()) {
                useVersion(detektConfig.toolVersion.get())
            }
        }
    }

    tasks.withType<Detekt>().configureEach {
        configureExcludes()
    }

    tasks.withType<DetektCreateBaselineTask>().configureEach {
        configureExcludes()
    }

    tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).configure {
        dependsOn(tasks.withType<Detekt>())
    }

    dependencies {
        detektPlugins("$DETEKT_GROUP_ID:detekt-rules-ktlint-wrapper")
    }
}

private fun SourceTask.configureExcludes() {
    val buildDir = project.layout.buildDirectory.get().asFile
    exclude { it.file.startsWith(buildDir) }
}

private fun DependencyHandlerScope.detektPlugins(dependencyNotation: Any): Dependency? =
    "detektPlugins"(dependencyNotation)

private const val DETEKT_GROUP_ID = "dev.detekt"
