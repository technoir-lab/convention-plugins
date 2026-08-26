package io.technoirlab.gradle

import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.io.Serial
import java.io.Serializable
import java.net.URI

/**
 * Represents a Maven artifact repository.
 */
data class MavenRepository(
    /**
     * The repository name.
     */
    val name: String,
    /**
     * The base URL of the repository.
     */
    val url: URI,
    /**
     * The credentials for authentication, if any.
     */
    val credentials: Credentials? = null,
) : Serializable {
    /**
     * Username/password credentials that can be used to log in to a password-protected remote repository.
     */
    data class Credentials(
        /**
         * The password to use when authenticating to the repository.
         */
        val username: String,
        /**
         * The password to use when authenticating to the repository.
         */
        val password: String,
    )

    private companion object {
        @Serial
        private const val serialVersionUID: Long = 7505042977132764269L
    }
}

fun RepositoryHandler.maven(repository: MavenRepository) {
    maven {
        name = repository.name
        url = repository.url
        repository.credentials?.let {
            credentials {
                username = it.username
                password = it.password
            }
        }
    }
}
