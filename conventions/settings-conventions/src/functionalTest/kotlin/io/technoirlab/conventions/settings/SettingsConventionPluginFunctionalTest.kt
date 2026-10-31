package io.technoirlab.conventions.settings

import io.technoirlab.gradle.test.kit.GradleRunnerExtension
import io.technoirlab.gradle.test.kit.appendBuildScript
import io.technoirlab.gradle.test.kit.buildDir
import io.technoirlab.gradle.test.kit.jarEntries
import io.technoirlab.gradle.test.kit.settingsScript
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.Path
import kotlin.io.path.appendText
import kotlin.io.path.div
import kotlin.io.path.writeText

class SettingsConventionPluginFunctionalTest {
    @RegisterExtension
    private val gradleRunner = GradleRunnerExtension("sample-project")

    @Test
    fun `dependency analysis`() {
        gradleRunner.build(":buildHealth")
    }

    @Test
    fun `dynamic versions are prohibited`() {
        gradleRunner.root.project("jvm-library").appendBuildScript(
            """
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.+")
            }
            """.trimIndent(),
        )

        val buildResult = gradleRunner.build(":jvm-library:jar", expectFailure = true)

        assertThat(buildResult.output).contains(
            "Could not resolve org.jetbrains.kotlin:kotlin-reflect:2.2.+: Resolution strategy disallows usage of dynamic versions",
        )
    }

    @Test
    fun `per-project repositories are prohibited`() {
        gradleRunner.root.appendBuildScript(
            """
            repositories {
                mavenCentral()
            }
            """.trimIndent(),
        )

        val buildResult = gradleRunner.build(":help", expectFailure = true)

        assertThat(buildResult.output).contains(
            "Build was configured to prefer settings repositories over project repositories but " +
                "repository 'MavenRepo' was added by build file 'build.gradle.kts'",
        )
    }

    @Test
    fun `Maven Central publishing`() {
        val buildResult = gradleRunner.build(":nmcpZipAggregation")

        assertThat(buildResult.task(":nmcpZipAggregation")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

        val entries = (gradleRunner.root.buildDir / "nmcp/zip/aggregation.zip").jarEntries()
        assertThat(entries.filter { it.endsWith(".md5") }).isNotEmpty
        assertThat(entries.filter { it.endsWith(".sha1") }).isNotEmpty
        // Optional checksums get filtered-out
        assertThat(entries.filter { it.endsWith(".sha256") }).isEmpty()
        assertThat(entries.filter { it.endsWith(".sha512") }).isEmpty()
    }

    @Nested
    inner class BuildCache {
        private val credentialsFile: Path
            get() = gradleRunner.root.dir / "credentials.json"

        @BeforeEach
        fun setUp() {
            credentialsFile.writeText("example-credentials")
            gradleRunner.root.settingsScript.appendText(
                $$"""

                gradle.settingsEvaluated {
                    val remote = buildCache.remote as androidx.build.gradle.gcpbuildcache.GcpBuildCache?
                    val remoteDescription = remote?.run {
                        val credentials = (credentials as androidx.build.gradle.gcpbuildcache.ExportedKeyGcpCredentials).credentials()
                        "$projectId/$bucketName, push=$isPush, credentials=$credentials"
                    }
                    println("Build cache: local=${buildCache.local.isEnabled}, remote=${remoteDescription ?: "none"}")
                }
                """.trimIndent(),
            )
        }

        @Test
        fun `uses only the local cache without remote cache settings`() {
            val buildResult = gradleRunner.build(":help") {
                environmentVariables += mapOf("GITHUB_ACTIONS" to "true")
            }

            assertThat(buildResult.output).contains("Build cache: local=true, remote=none")
        }

        @ParameterizedTest
        @ValueSource(strings = ["GCP_BUILD_CACHE_PROJECT_ID", "GCP_BUILD_CACHE_BUCKET_NAME", "GCP_BUILD_CACHE_CREDENTIALS"])
        fun `uses only the local cache when a remote cache setting is missing`(missingVariable: String) {
            val environment = remoteCacheEnvironment(push = "true") - missingVariable

            val buildResult = gradleRunner.build(":help") {
                environmentVariables += environment
            }

            assertThat(buildResult.output).contains("Build cache: local=true, remote=none")
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = ["false", "yes"])
        fun `reads the remote cache alongside the local cache without push`(push: String?) {
            val buildResult = gradleRunner.build(":help") {
                environmentVariables += remoteCacheEnvironment(push)
            }

            assertThat(buildResult.output).contains(
                "Build cache: local=true, remote=example-project/example-bucket, push=false, credentials=example-credentials",
            )
        }

        @ParameterizedTest
        @ValueSource(strings = ["true", "TRUE"])
        fun `pushes to the remote cache instead of the local cache`(push: String) {
            val buildResult = gradleRunner.build(":help") {
                environmentVariables += remoteCacheEnvironment(push)
            }

            assertThat(buildResult.output).contains(
                "Build cache: local=false, remote=example-project/example-bucket, push=true, credentials=example-credentials",
            )
        }

        @Test
        fun `keeps the local cache disabled when it is disabled before the convention applies`() {
            val initScript = (gradleRunner.root.dir / "disable-local-cache.init.gradle.kts").apply {
                writeText(
                    """
                    beforeSettings {
                        buildCache {
                            local {
                                isEnabled = false
                            }
                        }
                    }
                    """.trimIndent(),
                )
            }

            val buildResult = gradleRunner.build(":help") {
                environmentVariables += remoteCacheEnvironment()
                initScripts.add(initScript)
            }

            assertThat(buildResult.output).contains(
                "Build cache: local=false, remote=example-project/example-bucket, push=false, credentials=example-credentials",
            )
        }

        @Test
        fun `reuses the remote cache configuration from the configuration cache`() {
            gradleRunner.build(":help") {
                environmentVariables += remoteCacheEnvironment(push = "true")
            }

            val buildResult = gradleRunner.build(":help") {
                environmentVariables += remoteCacheEnvironment(push = "true")
            }

            assertThat(buildResult.output).contains("Reusing configuration cache.")
        }

        private fun remoteCacheEnvironment(push: String? = null): Map<String, Any> = buildMap {
            put("GCP_BUILD_CACHE_PROJECT_ID", "example-project")
            put("GCP_BUILD_CACHE_BUCKET_NAME", "example-bucket")
            put("GCP_BUILD_CACHE_CREDENTIALS", credentialsFile)
            if (push != null) {
                put("GCP_BUILD_CACHE_PUSH", push)
            }
        }
    }
}
