package io.technoirlab.conventions.gradle.plugin.internal

import org.assertj.core.api.Assertions.assertThat
import org.gradle.util.GradleVersion
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class GradleCompatibilityTest {
    @ParameterizedTest
    @CsvSource(
        "9.3.0,2.2.20",
        "9.2.0,2.2.20",
        "9.1.0,2.2.0",
        "9.0.0,2.2.0",
        "8.14.3,2.0.21",
        "8.13,2.0.21",
        "8.12.1,2.0.21",
        "8.11.1,2.0.20",
    )
    fun embeddedKotlinVersion(gradleVersion: String, expectedKotlinVersion: String) {
        val embeddedKotlinVersion = GradleVersion.version(gradleVersion).embeddedKotlinVersion
        assertThat(embeddedKotlinVersion).isEqualTo(expectedKotlinVersion)
    }

    @ParameterizedTest
    @CsvSource(
        "9.3.0,2.2",
        "9.2.0,2.2",
        "9.1.0,2.2",
        "9.0.0,2.2",
        "8.14.3,2.0",
        "8.13,2.0",
        "8.12.1,2.0",
        "8.11.1,2.0",
    )
    fun kotlinApiVersion(gradleVersion: String, expectedKotlinApiVersion: String) {
        val kotlinApiVersion = GradleVersion.version(gradleVersion).kotlinApiVersion
        assertThat(kotlinApiVersion.version).isEqualTo(expectedKotlinApiVersion)
    }
}
