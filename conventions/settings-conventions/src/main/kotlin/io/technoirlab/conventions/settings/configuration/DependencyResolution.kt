package io.technoirlab.conventions.settings.configuration

import io.technoirlab.gradle.Environment
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.api.initialization.resolve.RulesMode

@Suppress("UnstableApiUsage")
internal fun Settings.configureDependencyResolution(environment: Environment) {
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        rulesMode.set(RulesMode.FAIL_ON_PROJECT_RULES)

        repositories {
            if (!environment.isCi) {
                mavenLocal()
            }
            mavenCentral()
            google()
            gradlePluginPortal()
        }
    }
}

internal fun Project.configureDependencyResolution() {
    configurations.configureEach {
        resolutionStrategy {
            failOnChangingVersions()
            failOnDynamicVersions()
        }
    }
}
