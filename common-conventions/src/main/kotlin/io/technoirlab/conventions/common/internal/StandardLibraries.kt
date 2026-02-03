package io.technoirlab.conventions.common.internal

import io.technoirlab.conventions.common.BuildConfig
import org.apache.maven.artifact.versioning.ComparableVersion

internal class StandardLibraries(private val kotlinVersion: String) {
    private val kotlinVersionNumber = ComparableVersion(kotlinVersion)

    init {
        check(kotlinVersionNumber >= ComparableVersion("2.0.0")) { "Unsupported Kotlin version $kotlinVersion" }
    }

    val kotlinBom: String
        get() = "org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"

    val kotlinCoroutinesBom: String
        get() = "org.jetbrains.kotlinx:kotlinx-coroutines-bom:$kotlinCoroutinesVersion"

    val kotlinSerializationBom: String
        get() = "org.jetbrains.kotlinx:kotlinx-serialization-bom:$kotlinSerializationVersion"

    val kotlinCoroutinesVersion: String
        get() = when {
            kotlinVersionNumber < ComparableVersion("2.1.0") -> "1.9.0"
            else -> BuildConfig.KOTLINX_COROUTINES_VERSION
        }

    val kotlinSerializationVersion: String
        get() = when {
            kotlinVersionNumber < ComparableVersion("2.0.20") -> "1.7.1"
            kotlinVersionNumber < ComparableVersion("2.1.0") -> "1.7.3"
            kotlinVersionNumber < ComparableVersion("2.1.20") -> "1.8.0"
            kotlinVersionNumber < ComparableVersion("2.2.0") -> "1.8.1"
            kotlinVersionNumber < ComparableVersion("2.3.0") -> "1.9.0"
            else -> BuildConfig.KOTLINX_SERIALIZATION_VERSION
        }
}
