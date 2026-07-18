package io.technoirlab.conventions.gradle.plugin.internal

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.gradle.util.GradleVersion
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class GradleCompatibilityTest {
    @ParameterizedTest
    @CsvSource(
        "9.6.1,2.3.21",
        "9.5.1,2.3.20",
        "9.4.1,2.3.0",
        "9.3.1,2.2.21",
        "9.2.1,2.2.20",
        "9.1.0,2.2.0",
        "9.0.0,2.2.0",
        "8.14.5,2.0.21"
    )
    fun embeddedKotlinVersion(gradleVersion: String, expectedKotlinVersion: String) {
        val embeddedKotlinVersion = GradleVersion.version(gradleVersion).embeddedKotlinVersion
        assertThat(embeddedKotlinVersion).isEqualTo(expectedKotlinVersion)
    }

    @Test
    fun `embeddedKotlinVersion - unsupported Gradle version`() {
        val gradleVersion = GradleVersion.version("8.13.5")

        assertThatIllegalStateException()
            .isThrownBy { gradleVersion.embeddedKotlinVersion }
            .withMessage("Gradle 8.13.5 is unsupported")
    }

    @ParameterizedTest
    @CsvSource(
        "9.6.1,2.2",
        "9.5.1,2.2",
        "9.4.1,2.2",
        "9.3.1,2.2",
        "9.2.1,2.2",
        "9.1.0,2.2",
        "9.0.0,2.2",
        "8.14.5,2.0"
    )
    fun kotlinApiVersion(gradleVersion: String, expectedKotlinApiVersion: String) {
        val kotlinApiVersion = GradleVersion.version(gradleVersion).kotlinApiVersion
        assertThat(kotlinApiVersion.version).isEqualTo(expectedKotlinApiVersion)
    }

    @Test
    fun `kotlinApiVersion - unsupported Gradle version`() {
        val gradleVersion = GradleVersion.version("8.13.5")

        assertThatIllegalStateException()
            .isThrownBy { gradleVersion.kotlinApiVersion }
            .withMessage("Gradle 8.13.5 is unsupported")
    }
}
