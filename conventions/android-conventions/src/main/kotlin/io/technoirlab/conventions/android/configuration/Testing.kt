package io.technoirlab.conventions.android.configuration

import io.technoirlab.conventions.android.BuildConfig
import io.technoirlab.gradle.dependencies.testImplementation
import io.technoirlab.gradle.dependencies.testRuntimeOnly
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureTesting() {
    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:${BuildConfig.JUNIT_VERSION}")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:${BuildConfig.JUNIT_VERSION}")
    }
}
