package io.technoirlab.conventions.gradle.plugin.internal

import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

// See https://docs.gradle.org/current/userguide/compatibility.html#kotlin
internal val GradleVersion.embeddedKotlinVersion: String
    get() = when {
        this >= GradleVersion.version("9.3") -> "2.2.21"
        this >= GradleVersion.version("9.2") -> "2.2.20"
        this >= GradleVersion.version("9.0") -> "2.2.0"
        this >= GradleVersion.version("8.12") -> "2.0.21"
        this >= GradleVersion.version("8.11") -> "2.0.20"
        else -> error("Gradle version $this is unsupported")
    }

internal val GradleVersion.kotlinApiVersion: KotlinVersion
    get() = KotlinVersion.fromVersion(DefaultArtifactVersion(embeddedKotlinVersion).let { "${it.majorVersion}.${it.minorVersion}" })
