package io.technoirlab.conventions.common.configuration

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure

internal fun Project.configureJava() {
    pluginManager.withPlugin("java-base") {
        extensions.configure(JavaPluginExtension::class) {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(JDK_VERSION))
            }
        }
    }
}

internal const val JDK_VERSION = 21
