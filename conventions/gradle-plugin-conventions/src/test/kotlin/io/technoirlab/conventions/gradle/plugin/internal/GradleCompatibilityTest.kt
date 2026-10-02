package io.technoirlab.conventions.gradle.plugin.internal

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class GradleCompatibilityTest {
    @ParameterizedTest
    @CsvSource(
        "9.8.0,2.4.10",
        "9.7.1,2.4.0",
        "9.6.1,2.3.21",
        "9.5.1,2.3.20",
        "9.4.1,2.3.0",
        "9.3.1,2.2.21",
        "9.2.1,2.2.20",
        "9.1.0,2.2.0",
        "9.0.0,2.2.0",
    )
    fun `embedded Kotlin version matches the Gradle release`(gradleVersion: String, expectedKotlinVersion: String) {
        val embeddedKotlinVersion = GradleVersion.version(gradleVersion).embeddedKotlinVersion

        assertThat(embeddedKotlinVersion).isEqualTo(expectedKotlinVersion)
    }

    @ParameterizedTest
    @CsvSource(
        "9.8.0,2.2",
        "9.7.1,2.2",
        "9.6.1,2.2",
        "9.5.1,2.2",
        "9.4.1,2.2",
        "9.3.1,2.2",
        "9.2.1,2.2",
        "9.1.0,2.2",
        "9.0.0,2.2",
    )
    fun `build scripts use the supported Kotlin level`(gradleVersion: String, expectedKotlinVersion: String) {
        val buildScriptKotlinVersion = GradleVersion.version(gradleVersion).buildScriptKotlinVersion

        assertThat(buildScriptKotlinVersion.version).isEqualTo(expectedKotlinVersion)
    }

    @ParameterizedTest
    @CsvSource(
        "9.8.0,2.4",
        "9.7.1,2.4",
        "9.6.1,2.3",
        "9.5.1,2.3",
        "9.4.1,2.3",
        "9.3.1,2.2",
        "9.2.1,2.2",
        "9.1.0,2.2",
        "9.0.0,2.2",
    )
    fun `runtime uses the embedded Kotlin level`(gradleVersion: String, expectedKotlinVersion: String) {
        val runtimeKotlinVersion = GradleVersion.version(gradleVersion).runtimeKotlinVersion

        assertThat(runtimeKotlinVersion.version).isEqualTo(expectedKotlinVersion)
    }

    @ParameterizedTest
    @CsvSource(
        "9.8,2.2,2.4.10",
        "9.7,2.2,2.4.0",
        "9.6,2.2,2.3.21",
        "9.5,2.2,2.3.20",
        "9.4,2.2,2.3.0",
        "9.3,2.2,2.2.21",
        "9.2,2.2,2.2.20",
        "9.1,2.2,2.2.0",
        "9.0,2.2,2.2.0",
    )
    fun `API compilation uses the build script Kotlin level and embedded libraries`(
        gradleVersion: String,
        expectedKotlinVersion: String,
        expectedCoreLibrariesVersion: String,
    ) {
        val kotlinConfig = GradleVersion.version(gradleVersion).apiKotlinConfig

        assertThat(kotlinConfig.apiVersion.version).isEqualTo(expectedKotlinVersion)
        assertThat(kotlinConfig.languageVersion.version).isEqualTo(expectedKotlinVersion)
        assertThat(kotlinConfig.coreLibrariesVersion).isEqualTo(expectedCoreLibrariesVersion)
    }

    @ParameterizedTest
    @CsvSource(
        "9.8,2.4,2.4,2.4.10",
        "9.7,2.4,2.4,2.4.0",
        "9.6,2.3,2.4,2.3.21",
        "9.5,2.3,2.4,2.3.20",
        "9.4,2.3,2.4,2.3.0",
        "9.3,2.2,2.3,2.2.21",
        "9.2,2.2,2.3,2.2.20",
        "9.1,2.2,2.3,2.2.0",
        "9.0,2.2,2.3,2.2.0",
    )
    fun `implementation uses the runtime API level and next language level capped at the compiler default`(
        gradleVersion: String,
        expectedApiVersion: String,
        expectedLanguageVersion: String,
        expectedCoreLibrariesVersion: String,
    ) {
        val kotlinConfig = GradleVersion.version(gradleVersion).implementationKotlinConfig

        assertThat(kotlinConfig.apiVersion.version).isEqualTo(expectedApiVersion)
        assertThat(kotlinConfig.languageVersion.version).isEqualTo(expectedLanguageVersion)
        assertThat(kotlinConfig.apiVersion).isLessThanOrEqualTo(KotlinVersion.DEFAULT)
        assertThat(kotlinConfig.languageVersion).isLessThanOrEqualTo(KotlinVersion.DEFAULT)
        assertThat(kotlinConfig.coreLibrariesVersion).isEqualTo(expectedCoreLibrariesVersion)
    }

    @Test
    fun `embedded Kotlin version rejects unsupported Gradle versions`() {
        val gradleVersion = GradleVersion.version("8.14.5")

        assertThatIllegalStateException()
            .isThrownBy { gradleVersion.embeddedKotlinVersion }
            .withMessage("Gradle 8.14.5 is unsupported")
    }

    @Test
    fun `build script Kotlin version rejects unsupported Gradle versions`() {
        val gradleVersion = GradleVersion.version("8.14.5")

        assertThatIllegalStateException()
            .isThrownBy { gradleVersion.buildScriptKotlinVersion }
            .withMessage("Gradle 8.14.5 is unsupported")
    }

    @Test
    fun `runtime Kotlin version rejects unsupported Gradle versions`() {
        val gradleVersion = GradleVersion.version("8.14.5")

        assertThatIllegalStateException()
            .isThrownBy { gradleVersion.runtimeKotlinVersion }
            .withMessage("Gradle 8.14.5 is unsupported")
    }

    @Test
    fun `API Kotlin configuration rejects unsupported Gradle versions`() {
        val gradleVersion = GradleVersion.version("8.14.5")

        assertThatIllegalStateException()
            .isThrownBy { gradleVersion.apiKotlinConfig }
            .withMessage("Gradle 8.14.5 is unsupported")
    }

    @Test
    fun `implementation Kotlin configuration rejects unsupported Gradle versions`() {
        val gradleVersion = GradleVersion.version("8.14.5")

        assertThatIllegalStateException()
            .isThrownBy { gradleVersion.implementationKotlinConfig }
            .withMessage("Gradle 8.14.5 is unsupported")
    }
}
