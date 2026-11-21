package io.technoirlab.conventions.common.configuration

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation

internal fun Project.configureCoverage() {
    pluginManager.withPlugin("org.jetbrains.kotlinx.kover") {
        configure<KoverProjectExtension> {
            currentProject {
                // Kover measures every JVM compilation other than test unless it is told which ones to measure, so test
                // fixtures, benchmarks and other code that is not shipped would count as application code. Conventions
                // that ship more than main add their compilations to this list.
                sources {
                    includedSourceSets.add(KotlinCompilation.MAIN_COMPILATION_NAME)
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
