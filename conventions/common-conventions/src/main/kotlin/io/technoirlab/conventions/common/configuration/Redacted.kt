package io.technoirlab.conventions.common.configuration

import org.gradle.api.Project
import org.gradle.api.provider.Provider

fun Project.configureRedacted(enable: Provider<Boolean>) {
    if (!enable.get()) return

    pluginManager.apply("dev.zacsweers.redacted")
}
