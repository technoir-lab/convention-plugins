package io.technoirlab.conventions.common.configuration

import io.technoirlab.conventions.common.BuildConfig
import org.gradle.api.Project

internal fun Project.configureDependencyResolution() {
    configurations.configureEach {
        resolutionStrategy {
            failOnChangingVersions()
            failOnDynamicVersions()

            eachDependency {
                if (requested.group == BuildConfig.GROUP_ID && requested.version.isNullOrEmpty()) {
                    useVersion(BuildConfig.VERSION)
                }
            }
        }
    }
}
