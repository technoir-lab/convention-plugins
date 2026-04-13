package io.technoirlab.conventions.gradle.plugin.internal

import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

// See https://docs.gradle.org/current/userguide/compatibility.html#kotlin
internal val GradleVersion.embeddedKotlinVersion: String
    get() = when {
        this >= GradleVersion.version("9.5") -> "2.3.20"
        this >= GradleVersion.version("9.4") -> "2.3.0"
        this >= GradleVersion.version("9.3") -> "2.2.21"
        this >= GradleVersion.version("9.2") -> "2.2.20"
        this >= GradleVersion.version("9.0") -> "2.2.0"
        this >= GradleVersion.version("8.12") -> "2.0.21"
        this >= GradleVersion.version("8.11") -> "2.0.20"
        else -> error("Gradle version $this is unsupported")
    }

internal val GradleVersion.kotlinApiVersion: KotlinVersion
    get() = when {
        this >= GradleVersion.version("9.0") -> KotlinVersion.KOTLIN_2_2
        else -> KotlinVersion.KOTLIN_2_0
    }
