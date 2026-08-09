package io.technoirlab.conventions.kotlin.multiplatform.configuration

import org.gradle.api.Project
import org.gradle.api.provider.Provider

internal fun Project.configureMetro(enableMetro: Provider<Boolean>) {
    if (!enableMetro.get()) return

    pluginManager.apply("dev.zacsweers.metro")
}
