package io.technoirlab.conventions.settings.configuration

import nmcp.NmcpAggregationExtension
import nmcp.NmcpSettings
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

internal fun Settings.configurePublishing() {
    configure<NmcpSettings> {
        centralPortal {
            publishingType.set("AUTOMATIC")
        }
    }
}

internal fun Project.configurePublishing() {
    pluginManager.withPlugin("com.gradleup.nmcp.aggregation") {
        configure<NmcpAggregationExtension> {
            allowDuplicateProjectNames.set(true)
            // Set lazily on the project: Nmcp settings options pass through an isolated action that cannot carry environment providers.
            centralPortal {
                username.set(providers.environmentVariable("CENTRAL_PORTAL_USER"))
                password.set(providers.environmentVariable("CENTRAL_PORTAL_PASSWORD"))
            }
        }

        tasks.withType<Zip>()
            .matching { it.name.startsWith("nmcpZip") }
            .configureEach {
                // Maven Central requires MD5 and SHA-1; SHA-512 is optional and increases the published file count.
                exclude("**/*.sha512")
            }
    }
}
