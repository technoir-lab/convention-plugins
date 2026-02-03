package io.technoirlab.conventions.common.internal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class StandardLibrariesTest {
    @Test
    fun kotlinBom() {
        val kotlinBom = StandardLibraries("2.2.21").kotlinBom
        assertThat(kotlinBom).isEqualTo("org.jetbrains.kotlin:kotlin-bom:2.2.21")
    }

    @Test
    fun kotlinCoroutinesBom() {
        val kotlinCoroutinesBom = StandardLibraries("2.2.21").kotlinCoroutinesBom
        assertThat(kotlinCoroutinesBom).isEqualTo("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2")
    }

    @Test
    fun kotlinSerializationBom() {
        val kotlinSerializationBom = StandardLibraries("2.2.21").kotlinSerializationBom
        assertThat(kotlinSerializationBom).isEqualTo("org.jetbrains.kotlinx:kotlinx-serialization-bom:1.9.0")
    }

    @ParameterizedTest
    @CsvSource(
        "2.3.21,1.10.2",
        "2.3.20,1.10.2",
        "2.3.10,1.10.2",
        "2.3.0,1.10.2",
        "2.2.21,1.10.2",
        "2.2.20,1.10.2",
        "2.2.10,1.10.2",
        "2.2.0,1.10.2",
        "2.1.21,1.10.2",
        "2.1.20,1.10.2",
        "2.1.10,1.10.2",
        "2.1.0,1.10.2",
        "2.0.21,1.9.0",
        "2.0.20,1.9.0",
        "2.0.10,1.9.0",
        "2.0.0,1.9.0"
    )
    fun kotlinCoroutinesVersion(kotlinVersion: String, expectedKotlinCoroutinesVersion: String) {
        val kotlinCoroutinesVersion = StandardLibraries(kotlinVersion).kotlinCoroutinesVersion
        assertThat(kotlinCoroutinesVersion).isEqualTo(expectedKotlinCoroutinesVersion)
    }

    @ParameterizedTest
    @CsvSource(
        "2.3.21,1.10.0",
        "2.3.20,1.10.0",
        "2.3.10,1.10.0",
        "2.3.0,1.10.0",
        "2.2.21,1.9.0",
        "2.2.20,1.9.0",
        "2.2.10,1.9.0",
        "2.2.0,1.9.0",
        "2.1.21,1.8.1",
        "2.1.20,1.8.1",
        "2.1.10,1.8.0",
        "2.1.0,1.8.0",
        "2.0.21,1.7.3",
        "2.0.20,1.7.3",
        "2.0.10,1.7.1",
        "2.0.0,1.7.1"
    )
    fun kotlinSerializationVersion(kotlinVersion: String, expectedKotlinSerializationVersion: String) {
        val kotlinSerializationVersion = StandardLibraries(kotlinVersion).kotlinSerializationVersion
        assertThat(kotlinSerializationVersion).isEqualTo(expectedKotlinSerializationVersion)
    }
}
