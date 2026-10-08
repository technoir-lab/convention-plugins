package io.technoirlab.conventions.common.configuration

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureCoverage() {
    pluginManager.withPlugin("org.jetbrains.kotlinx.kover") {
        configure<KoverProjectExtension> {
            currentProject {
                instrumentation {
                    disabledForTestTasks.add("functionalTest")
                }
            }

            reports {
                filters {
                    excludes {
                        annotatedBy("javax.annotation.processing.Generated")
                    }
                }
                total {
                    html {
                        onCheck.set(true)
                    }
                }
            }
        }
    }
}
