package io.technoirlab.gradle.test.kit

import org.gradle.util.GradleVersion

internal fun String.toGradleVersion(): GradleVersion = GradleVersion.version(this)

internal fun GradleVersion.toFullVersion(): GradleVersion =
    if (SHORT_SEMANTIC_VERSION.matches(version) && majorVersion >= 9) GradleVersion.version("$version.0") else this

private val SHORT_SEMANTIC_VERSION = Regex("""\d+\.\d+""")
