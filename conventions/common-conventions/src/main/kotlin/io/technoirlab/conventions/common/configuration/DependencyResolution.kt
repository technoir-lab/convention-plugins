package io.technoirlab.conventions.common.configuration

import com.android.Version
import io.technoirlab.conventions.common.BuildConfig
import org.gradle.api.Project

internal fun Project.configureDependencyResolution() {
    configurations.configureEach {
        resolutionStrategy {
            failOnChangingVersions()
            failOnDynamicVersions()

            eachDependency {
                if (requested.version.isNullOrEmpty()) {
                    when (requested.group) {
                        BuildConfig.GROUP_ID -> useVersion(BuildConfig.VERSION)
                        "com.android.tools.lint" -> useVersion(Version.ANDROID_TOOLS_BASE_VERSION)
                    }
                }
            }
        }
    }
}
