package io.technoirlab.conventions.common.api.metadata

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

/**
 * Information about the project.
 */
interface ProjectMetadata {
    /**
     * The project's name.
     */
    val name: Property<String>

    /**
     * The project's description.
     */
    val description: Property<String>

    /**
     * The project's URL.
     */
    val url: Property<String>

    /**
     * The project's developers.
     */
    val developers: ListProperty<DeveloperInfo>

    /**
     * The project's licenses.
     */
    val licenses: ListProperty<LicenseInfo>

    /**
     * Add information about a project developer.
     */
    fun developer(id: String? = null, name: String, email: String? = null, organization: String? = null, organizationUrl: String? = null) {
        developers.add(DeveloperInfo(id, name, email, organization, organizationUrl))
    }

    /**
     * Add information about a project's license.
     */
    fun license(name: String, url: String, distribution: String? = null) {
        licenses.add(LicenseInfo(name, url, distribution))
    }
}
