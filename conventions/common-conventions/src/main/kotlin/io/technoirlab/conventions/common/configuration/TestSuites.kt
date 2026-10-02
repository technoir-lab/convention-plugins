@file:Suppress("UnstableApiUsage")

package io.technoirlab.conventions.common.configuration

import io.technoirlab.conventions.common.BuildConfig
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.base.TestingExtension

internal fun Project.configureTestSuites() {
    pluginManager.withPlugin("jvm-test-suite") {
        extensions.configure(TestingExtension::class) {
            suites.named { it == DEFAULT_TEST_SUITE }.withType<JvmTestSuite>().configureEach {
                configureTestSuite {}
            }
        }
    }
}

fun JvmTestSuite.configureTestSuite(testTaskConfiguration: Action<Test>) {
    useJUnitJupiter(BuildConfig.JUNIT_VERSION)

    targets.configureEach {
        testTask.configure(testTaskConfiguration)
    }
}

private const val DEFAULT_TEST_SUITE = "test"
