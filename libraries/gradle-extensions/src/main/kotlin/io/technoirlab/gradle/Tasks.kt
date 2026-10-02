package io.technoirlab.gradle

import org.gradle.api.Task

fun Task.disable() {
    enabled = false
    group = null
}
