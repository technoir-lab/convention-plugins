package io.technoirlab.gradle

import io.technoirlab.gradle.test.kit.StubProviderFactory
import io.technoirlab.gradle.test.kit.createRootProject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

class EnvironmentTest {
    private val providers = StubProviderFactory(createRootProject("environment-fixture").providers)
    private val environment = Environment(providers)

    @Test
    fun `getMavenRepositories ignores credentials of a repository without a URL`() {
        providers.gradleProperties["publish.github.username"] = "user"
        providers.gradleProperties["publish.github.password"] = "password"
        providers.gradleProperties["publish.local.url"] = "https://example.org/repo"

        val repositories = environment.getMavenRepositories("publish").get()

        assertThat(repositories).containsExactly(MavenRepository(name = "local", url = URI("https://example.org/repo")))
    }
}
