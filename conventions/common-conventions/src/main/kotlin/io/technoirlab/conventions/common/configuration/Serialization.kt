package io.technoirlab.conventions.common.configuration

import org.gradle.api.Project
import org.gradle.api.provider.Provider

fun Project.configureKotlinSerialization(enable: Provider<Boolean>) {
    if (!enable.get()) return

    pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
}
