package io.technoirlab.gradle

import org.gradle.api.Project

fun Project.whenPluginApplied(id: String, action: () -> Unit) {
    var pluginApplied = false
    pluginManager.withPlugin(id) {
        pluginApplied = true
        action()
    }
    afterEvaluate {
        check(pluginApplied) { "Plugin '$id' is not applied" }
    }
}
