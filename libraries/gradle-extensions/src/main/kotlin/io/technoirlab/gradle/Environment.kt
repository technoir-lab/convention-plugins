package io.technoirlab.gradle

import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import java.net.URI

class Environment(private val providerFactory: ProviderFactory) {
    val isCi: Boolean
        get() = providerFactory.environmentVariable("GITHUB_ACTIONS").isPresent

    val isLaunchedFromTest: Boolean
        get() = providerFactory.systemProperty("org.gradle.test.worker").isPresent

    val refName: Provider<String>
        get() = providerFactory.environmentVariable("GITHUB_REF_NAME")

    val tagName: Provider<String>
        get() = providerFactory.environmentVariable("GITHUB_REF_TYPE")
            .filter { it == "tag" }
            .flatMap { refName }

    val repositoryUrl: Provider<URI>
        get() = providerFactory.environmentVariable("GITHUB_SERVER_URL")
            .zip(providerFactory.environmentVariable("GITHUB_REPOSITORY")) { serverUrl, repository ->
                URI(serverUrl).resolve(repository)
            }

    val vcsUrl: Provider<String>
        get() = repositoryUrl.map { "$it.git" }

    fun getMavenRepositories(prefix: String): Provider<List<MavenRepository>> = providerFactory.gradlePropertiesPrefixedBy("$prefix.")
        .map { properties ->
            // Credentials can be shared by all builds, e.g. in the Gradle user home, while only some builds declare the repository.
            val names = properties.keys
                .map { it.removePrefix("$prefix.") }
                .filter { it.endsWith(".url") }
                .map { it.removeSuffix(".url") }
            names.map { name -> createMavenRepository(name, prefix, properties) }
        }

    private fun createMavenRepository(name: String, prefix: String, properties: Map<String, String>): MavenRepository {
        val usernameProperty = "$prefix.$name.username"
        val passwordProperty = "$prefix.$name.password"
        return MavenRepository(
            name = name,
            url = URI.create(properties.getValue("$prefix.$name.url")),
            credentials = if (usernameProperty in properties || passwordProperty in properties) {
                MavenRepository.Credentials(
                    username = properties.getValue(usernameProperty),
                    password = properties.getValue(passwordProperty),
                )
            } else {
                null
            },
        )
    }
}
