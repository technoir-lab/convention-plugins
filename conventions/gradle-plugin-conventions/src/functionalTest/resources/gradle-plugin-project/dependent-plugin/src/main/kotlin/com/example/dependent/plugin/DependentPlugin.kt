package com.example.dependent.plugin

import com.example.plugin.api.ExampleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class DependentPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.getByType<ExampleExtension>()
    }
}
