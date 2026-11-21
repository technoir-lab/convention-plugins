package io.technoirlab.conventions.gradle.plugin.configuration

import com.autonomousapps.DependencyAnalysisExtension
import com.autonomousapps.DependencyAnalysisSubExtension
import com.autonomousapps.extension.ProjectIssueHandler
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

internal fun Project.configureDependencyAnalysis() {
    pluginManager.withPlugin("com.autonomousapps.dependency-analysis") {
        extensions.findByType<DependencyAnalysisSubExtension>()?.issues {
            excludeApiVariant(path)
        }
        extensions.findByType<DependencyAnalysisExtension>()?.issues {
            project(path) {
                excludeApiVariant(path)
            }
        }
    }
}

private fun ProjectIssueHandler.excludeApiVariant(projectPath: String) {
    // Do not suggest changing the dependency on api feature variant from api to implementation
    onIncorrectConfiguration {
        exclude(projectPath)
    }
    // Do not report the dependency on api feature variant as unused
    onUnusedDependencies {
        exclude(projectPath)
    }
}
