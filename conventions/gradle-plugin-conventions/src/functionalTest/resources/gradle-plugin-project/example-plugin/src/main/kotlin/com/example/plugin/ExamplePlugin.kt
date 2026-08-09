package com.example.plugin

import com.example.plugin.api.ExampleExtension
import com.example.plugin.internal.ExampleExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

/**
 * Example Gradle plugin.
 */
class ExamplePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(ExampleExtension::class, ExampleExtension.NAME, ExampleExtensionImpl::class)
        // body placeholder
    }

    // function placeholder
}
