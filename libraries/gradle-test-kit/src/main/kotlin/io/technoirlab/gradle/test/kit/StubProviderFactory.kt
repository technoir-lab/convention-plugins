package io.technoirlab.gradle.test.kit

import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

/**
 * A [ProviderFactory] that resolves Gradle properties, system properties, and environment variables
 * from mutable in-memory maps instead of the build environment.
 *
 * Every other operation is forwarded to [delegate]. Lookups are evaluated lazily, so map changes made
 * after a provider is created are visible when the provider is queried. Missing keys produce absent providers.
 */
class StubProviderFactory(private val delegate: ProviderFactory) : ProviderFactory by delegate {
    val gradleProperties = mutableMapOf<String, String>()
    val systemProperties = mutableMapOf<String, String>()
    val environmentVariables = mutableMapOf<String, String>()

    override fun gradleProperty(propertyName: String): Provider<String> = lookup(gradleProperties, propertyName)

    override fun gradleProperty(propertyName: Provider<String>): Provider<String> = propertyName.flatMap { gradleProperty(it) }

    override fun gradlePropertiesPrefixedBy(propertyNamePrefix: String): Provider<Map<String, String>> =
        lookupPrefixed(gradleProperties, propertyNamePrefix)

    override fun gradlePropertiesPrefixedBy(propertyNamePrefix: Provider<String>): Provider<Map<String, String>> =
        propertyNamePrefix.flatMap { gradlePropertiesPrefixedBy(it) }

    override fun systemProperty(propertyName: String): Provider<String> = lookup(systemProperties, propertyName)

    override fun systemProperty(propertyName: Provider<String>): Provider<String> = propertyName.flatMap { systemProperty(it) }

    override fun systemPropertiesPrefixedBy(propertyNamePrefix: String): Provider<Map<String, String>> =
        lookupPrefixed(systemProperties, propertyNamePrefix)

    override fun systemPropertiesPrefixedBy(propertyNamePrefix: Provider<String>): Provider<Map<String, String>> =
        propertyNamePrefix.flatMap { systemPropertiesPrefixedBy(it) }

    override fun environmentVariable(variableName: String): Provider<String> = lookup(environmentVariables, variableName)

    override fun environmentVariable(variableName: Provider<String>): Provider<String> = variableName.flatMap { environmentVariable(it) }

    override fun environmentVariablesPrefixedBy(variableNamePrefix: String): Provider<Map<String, String>> =
        lookupPrefixed(environmentVariables, variableNamePrefix)

    override fun environmentVariablesPrefixedBy(variableNamePrefix: Provider<String>): Provider<Map<String, String>> =
        variableNamePrefix.flatMap { environmentVariablesPrefixedBy(it) }

    private fun lookup(values: Map<String, String>, name: String): Provider<String> = delegate.provider { values[name] }

    private fun lookupPrefixed(values: Map<String, String>, prefix: String): Provider<Map<String, String>> =
        delegate.provider { values.filterKeys { it.startsWith(prefix) } }
}
