package io.technoirlab.conventions.gradle.plugin.internal

import io.technoirlab.conventions.common.configuration.KotlinConfig
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

/**
 * Kotlin core-library version embedded in this Gradle release, including the patch version.
 * Used to align Kotlin core libraries with the minimum supported Gradle runtime.
 *
 * See the [Gradle Kotlin compatibility matrix](https://docs.gradle.org/current/userguide/compatibility.html#kotlin).
 */
internal val GradleVersion.embeddedKotlinVersion: String
    get() = when {
        this >= GradleVersion.version("9.8") -> "2.4.10"
        this >= GradleVersion.version("9.7") -> "2.4.0"
        this >= GradleVersion.version("9.6") -> "2.3.21"
        this >= GradleVersion.version("9.5") -> "2.3.20"
        this >= GradleVersion.version("9.4") -> "2.3.0"
        this >= GradleVersion.version("9.3") -> "2.2.21"
        this >= GradleVersion.version("9.2") -> "2.2.20"
        this >= GradleVersion.version("9.0") -> "2.2.0"
        else -> error("$this is unsupported")
    }

/**
 * Kotlin language and API level used to compile Gradle Kotlin DSL build scripts.
 * Public plugin APIs use this level so that build scripts can consume them.
 */
internal val GradleVersion.buildScriptKotlinVersion: KotlinVersion
    get() = when {
        this >= GradleVersion.version("9.0") -> KotlinVersion.KOTLIN_2_2
        else -> error("$this is unsupported")
    }

/**
 * Kotlin language and API level of the runtime embedded in Gradle.
 * This can be newer than the level used to compile build scripts.
 */
internal val GradleVersion.runtimeKotlinVersion: KotlinVersion
    get() = when {
        this >= GradleVersion.version("9.7") -> KotlinVersion.KOTLIN_2_4
        this >= GradleVersion.version("9.4") -> KotlinVersion.KOTLIN_2_3
        this >= GradleVersion.version("9.0") -> KotlinVersion.KOTLIN_2_2
        else -> error("$this is unsupported")
    }

/**
 * Configures public plugin APIs for consumption by Gradle Kotlin DSL build scripts.
 * The language and API levels use the lower of the build-script level and the compiler default.
 * Core libraries stay aligned with the embedded runtime.
 */
internal val GradleVersion.apiKotlinConfig: KotlinConfig
    get() = KotlinConfig(
        apiVersion = minOf(buildScriptKotlinVersion, KotlinVersion.DEFAULT),
        coreLibrariesVersion = embeddedKotlinVersion,
    )

/**
 * Uses the lower of the embedded runtime's API level and the compiler default for implementation APIs.
 * Uses the lower of the next language version after the embedded runtime's version and the compiler default
 * for implementation syntax. Core libraries stay aligned with the embedded runtime.
 */
internal val GradleVersion.implementationKotlinConfig: KotlinConfig
    get() = KotlinConfig(
        apiVersion = minOf(runtimeKotlinVersion, KotlinVersion.DEFAULT),
        languageVersion = KotlinVersion.entries.getOrElse(runtimeKotlinVersion.ordinal + 1) { KotlinVersion.DEFAULT }
            .coerceAtMost(KotlinVersion.DEFAULT),
        coreLibrariesVersion = embeddedKotlinVersion,
    )
