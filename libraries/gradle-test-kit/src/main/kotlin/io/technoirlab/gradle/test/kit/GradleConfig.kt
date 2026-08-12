package io.technoirlab.gradle.test.kit

import java.nio.file.Path

@Suppress("LongParameterList")
class GradleConfig internal constructor(
    var buildCache: Boolean = false,
    var configurationCache: Boolean = true,
    var configurationOnDemand: Boolean = true,
    var dryRun: Boolean = false,
    var isolatedProjects: Boolean = true,
    var warningsAsErrors: Boolean = false,
    var gradleVersion: String? = null,
    val arguments: MutableList<String> = mutableListOf("--stacktrace"),
    val initScripts: MutableList<Path> = mutableListOf(),
    val gradleProperties: MutableMap<String, Any> = mutableMapOf(),
    val systemProperties: MutableMap<String, Any> = mutableMapOf(),
    val environmentVariables: MutableMap<String, Any> = mutableMapOf(),
) {
    constructor(copy: GradleConfig) : this(
        buildCache = copy.buildCache,
        configurationCache = copy.configurationCache,
        configurationOnDemand = copy.configurationOnDemand,
        dryRun = copy.dryRun,
        isolatedProjects = copy.isolatedProjects,
        warningsAsErrors = copy.warningsAsErrors,
        gradleVersion = copy.gradleVersion,
        arguments = copy.arguments.toMutableList(),
        initScripts = copy.initScripts.toMutableList(),
        gradleProperties = copy.gradleProperties.toMutableMap(),
        systemProperties = copy.systemProperties.toMutableMap(),
        environmentVariables = copy.environmentVariables.toMutableMap(),
    )
}
