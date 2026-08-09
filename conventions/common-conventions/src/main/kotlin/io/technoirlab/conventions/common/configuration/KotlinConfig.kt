package io.technoirlab.conventions.common.configuration

import io.technoirlab.conventions.common.BuildConfig
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

data class KotlinConfig(
    val apiVersion: KotlinVersion,
    val languageVersion: KotlinVersion = apiVersion,
    val coreLibrariesVersion: String
) {
    companion object {
        val DEFAULT = KotlinConfig(
            apiVersion = KotlinVersion.DEFAULT,
            languageVersion = KotlinVersion.DEFAULT,
            coreLibrariesVersion = BuildConfig.KOTLIN_VERSION
        )
    }
}
