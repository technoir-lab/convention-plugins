@file:Suppress("UnstableApiUsage")

package io.technoirlab.conventions.common.configuration

import io.technoirlab.conventions.common.BuildConfig
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.testing.base.TestingExtension

fun Project.configureTesting() {
    pluginManager.apply("jvm-test-suite")

    extensions.configure(TestingExtension::class) {
        suites.named<JvmTestSuite>(DEFAULT_TEST_SUITE).configure {
            configureTestSuite {}
        }
    }
}

fun JvmTestSuite.configureTestSuite(testTaskConfiguration: Action<Test>) {
    useJUnitJupiter(BuildConfig.JUNIT5_VERSION)

    targets.configureEach {
        testTask.configure(testTaskConfiguration)
    }
}

private const val DEFAULT_TEST_SUITE = "test"
