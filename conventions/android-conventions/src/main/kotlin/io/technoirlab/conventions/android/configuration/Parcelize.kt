package io.technoirlab.conventions.android.configuration

import org.gradle.api.Project
import org.gradle.api.provider.Provider

internal fun Project.configureKotlinParcelize(enable: Provider<Boolean>) {
    if (!enable.get()) return

    pluginManager.apply("org.jetbrains.kotlin.plugin.parcelize")
}
