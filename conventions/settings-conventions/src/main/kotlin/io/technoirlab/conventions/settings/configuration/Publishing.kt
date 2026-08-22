package io.technoirlab.conventions.settings.configuration

import nmcp.NmcpAggregationExtension
import nmcp.NmcpSettings
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

internal fun Settings.configurePublishing() {
    // TODO: Remove .orNull when Nmcp bug is fixed
    val centralPortalUsername = providers.environmentVariable("CENTRAL_PORTAL_USER").orNull
    val centralPortalPassword = providers.environmentVariable("CENTRAL_PORTAL_PASSWORD").orNull
    extensions.configure(NmcpSettings::class) {
        centralPortal {
            username.set(centralPortalUsername)
            password.set(centralPortalPassword)
            publishingType.set("USER_MANAGED")
        }
    }
}

internal fun Project.configurePublishing() {
    pluginManager.withPlugin("com.gradleup.nmcp.aggregation") {
        extensions.configure<NmcpAggregationExtension> {
            allowDuplicateProjectNames.set(true)
        }

        tasks.withType<Zip>()
            .matching { it.name.startsWith("nmcpZip") }
            .configureEach {
                // Maven Central requires MD5 and SHA-1; SHA-512 is optional and increases the published file count.
                exclude("**/*.sha512")
            }
    }
}
