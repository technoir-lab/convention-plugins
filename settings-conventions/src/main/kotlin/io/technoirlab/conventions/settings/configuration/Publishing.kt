package io.technoirlab.conventions.settings.configuration

import nmcp.NmcpAggregationExtension
import nmcp.NmcpSettings
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.configure

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
    }
}
