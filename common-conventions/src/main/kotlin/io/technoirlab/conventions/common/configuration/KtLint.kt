package io.technoirlab.conventions.common.configuration

import io.technoirlab.conventions.common.BuildConfig
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension

internal fun Project.configureKtLint() {
    pluginManager.withPlugin("org.jlleitschuh.gradle.ktlint") {
        extensions.configure(KtlintExtension::class) {
            version.set(BuildConfig.KTLINT_VERSION)
        }
    }
}
