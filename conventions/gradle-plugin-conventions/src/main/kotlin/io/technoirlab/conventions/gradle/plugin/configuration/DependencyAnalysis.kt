package io.technoirlab.conventions.gradle.plugin.configuration

import com.autonomousapps.DependencyAnalysisSubExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureDependencyAnalysis() {
    pluginManager.withPlugin("com.autonomousapps.dependency-analysis") {
        extensions.configure(DependencyAnalysisSubExtension::class) {
            issues {
                // Do not suggest changing the dependency on api feature variant from api to implementation
                onIncorrectConfiguration {
                    exclude(path)
                }
                // Do not report the dependency on api feature variant as unused
                onUnusedDependencies {
                    exclude(path)
                }
            }
        }
    }
}
