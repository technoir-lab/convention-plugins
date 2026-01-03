package io.technoirlab.conventions.root.configuration

import nmcp.NmcpAggregationExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configurePublishing() {
    pluginManager.apply("com.gradleup.nmcp.aggregation")

    extensions.configure(NmcpAggregationExtension::class) {
        centralPortal {
            username.set(providers.environmentVariable("CENTRAL_PORTAL_USER"))
            password.set(providers.environmentVariable("CENTRAL_PORTAL_PASSWORD"))
            publishingType.set("USER_MANAGED")
            allowEmptyAggregation.set(true)
        }
    }
}
