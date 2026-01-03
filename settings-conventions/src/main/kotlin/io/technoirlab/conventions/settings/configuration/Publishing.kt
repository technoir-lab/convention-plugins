package io.technoirlab.conventions.settings.configuration

import nmcp.NmcpSettings
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
