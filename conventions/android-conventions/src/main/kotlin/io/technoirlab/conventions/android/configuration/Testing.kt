package io.technoirlab.conventions.android.configuration

import com.android.build.api.dsl.CommonExtension
import io.technoirlab.conventions.android.BuildConfig
import io.technoirlab.gradle.dependencies.testImplementation
import io.technoirlab.gradle.dependencies.testRuntimeOnly
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureUnitTests(androidExtension: CommonExtension) {
    androidExtension.testOptions.unitTests.all { test ->
        test.useJUnitPlatform()
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:${BuildConfig.JUNIT_VERSION}")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:${BuildConfig.JUNIT_VERSION}")
    }
}
