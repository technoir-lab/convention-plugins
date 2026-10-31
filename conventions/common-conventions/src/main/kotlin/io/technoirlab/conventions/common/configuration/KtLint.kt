package io.technoirlab.conventions.common.configuration

import io.technoirlab.conventions.common.BuildConfig
import io.technoirlab.gradle.asPath
import io.technoirlab.gradle.setDisallowChanges
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask
import java.io.File

internal fun Project.configureKtLint() {
    pluginManager.withPlugin("org.jlleitschuh.gradle.ktlint") {
        configure<KtlintExtension> {
            coloredOutput.setDisallowChanges(false)
            relative.setDisallowChanges(true)
            version.setDisallowChanges(BuildConfig.KTLINT_VERSION)

            reporters {
                reporter(ReporterType.PLAIN)
                reporter(ReporterType.SARIF)
            }

            filter {
                val buildDirectory = layout.buildDirectory.get().asPath().toAbsolutePath().normalize()
                exclude { it.file.toPath().toAbsolutePath().normalize().startsWith(buildDirectory) }
            }
        }

        // Check outputs record absolute source paths, which reports relativize against the project directory.
        // Paths from a host with a different separator have a different root, so keep Windows and Unix cache entries apart.
        tasks.withType<BaseKtLintCheckTask>().configureEach {
            inputs.property("fileSeparator", File.separator)
        }

        configurations.configureEach {
            resolutionStrategy.eachDependency {
                if (requested.name == "com.pinterest.ktlint" && requested.version.isNullOrEmpty()) {
                    useVersion(BuildConfig.KTLINT_VERSION)
                }
            }
        }

        dependencies {
            "ktlintRuleset"("${BuildConfig.GROUP_ID}:ktlint-rules")
        }
    }
}
