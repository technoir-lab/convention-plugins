package io.technoirlab.conventions.settings.configuration

import com.autonomousapps.DependencyAnalysisExtension
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.configure

internal fun Settings.configureDependencyAnalysis() {
    extensions.configure(DependencyAnalysisExtension::class) {
        structure {
            bundle("junit-jupiter") {
                primary("org.junit.jupiter:junit-jupiter")
                include("org.junit.jupiter:junit-jupiter-api")
                include("org.junit.jupiter:junit-jupiter-params")
            }
            bundle("kotlin-gradle-plugin") {
                primary("org.jetbrains.kotlin:kotlin-gradle-plugin")
                include("org.jetbrains.kotlin:kotlin-native-utils")
            }
        }
        issues {
            all {
                onIncorrectConfiguration { severity("ignore") }
                onUnusedDependencies {
                    exclude("org.junit.jupiter:junit-jupiter")
                }
                onUsedTransitiveDependencies {
                    exclude("org.jetbrains.kotlin:kotlin-parcelize-runtime")
                }
            }
        }
        abi {
            exclusions {
                ignoreGeneratedCode()
                ignoreInternalPackages()
            }
        }
    }
}
