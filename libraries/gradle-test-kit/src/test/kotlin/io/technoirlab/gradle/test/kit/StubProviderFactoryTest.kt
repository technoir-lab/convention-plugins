package io.technoirlab.gradle.test.kit

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.Test

class StubProviderFactoryTest {
    private val delegate = createRootProject("stub-provider-factory").providers
    private val providers = StubProviderFactory(delegate)

    @Test
    fun `unset values produce absent providers`() {
        val gradleProperty = providers.gradleProperty("org.gradle.jvmargs")
        val systemProperty = providers.systemProperty("user.home")
        val environmentVariable = providers.environmentVariable("PATH")

        assertThat(gradleProperty.isPresent).isFalse()
        assertThat(systemProperty.isPresent).isFalse()
        assertThat(environmentVariable.isPresent).isFalse()
    }

    @Test
    fun `values are resolved from the corresponding maps`() {
        providers.gradleProperties["name"] = "gradle"
        providers.systemProperties["name"] = "system"
        providers.environmentVariables["NAME"] = "environment"

        val gradleProperty = providers.gradleProperty("name").get()
        val systemProperty = providers.systemProperty("name").get()
        val environmentVariable = providers.environmentVariable("NAME").get()

        assertThat(gradleProperty).isEqualTo("gradle")
        assertThat(systemProperty).isEqualTo("system")
        assertThat(environmentVariable).isEqualTo("environment")
    }

    @Test
    fun `values set after provider creation are visible`() {
        val provider = providers.gradleProperty("name")
        providers.gradleProperties["name"] = "late"

        val value = provider.get()

        assertThat(value).isEqualTo("late")
    }

    @Test
    fun `provider name overloads resolve from the corresponding maps`() {
        providers.gradleProperties["name"] = "gradle"
        providers.systemProperties["name"] = "system"
        providers.environmentVariables["NAME"] = "environment"

        val gradleProperty = providers.gradleProperty(delegate.provider { "name" }).get()
        val systemProperty = providers.systemProperty(delegate.provider { "name" }).get()
        val environmentVariable = providers.environmentVariable(delegate.provider { "NAME" }).get()

        assertThat(gradleProperty).isEqualTo("gradle")
        assertThat(systemProperty).isEqualTo("system")
        assertThat(environmentVariable).isEqualTo("environment")
    }

    @Test
    fun `prefixed lookups return only matching entries`() {
        providers.gradleProperties.putAll(mapOf("org.gradle.caching" to "true", "kotlin.code.style" to "official"))
        providers.systemProperties.putAll(mapOf("user.home" to "home", "java.version" to "25"))
        providers.environmentVariables.putAll(mapOf("GITHUB_ACTIONS" to "true", "HOME" to "home"))

        val gradleProperties = providers.gradlePropertiesPrefixedBy("org.gradle.").get()
        val systemProperties = providers.systemPropertiesPrefixedBy(delegate.provider { "user." }).get()
        val environmentVariables = providers.environmentVariablesPrefixedBy("GITHUB_").get()

        assertThat(gradleProperties).containsExactly(entry("org.gradle.caching", "true"))
        assertThat(systemProperties).containsExactly(entry("user.home", "home"))
        assertThat(environmentVariables).containsExactly(entry("GITHUB_ACTIONS", "true"))
    }

    @Test
    fun `prefixed lookups without matches return empty maps`() {
        providers.gradleProperties["kotlin.code.style"] = "official"

        val gradleProperties = providers.gradlePropertiesPrefixedBy("org.gradle.").get()

        assertThat(gradleProperties).isEmpty()
    }
}
