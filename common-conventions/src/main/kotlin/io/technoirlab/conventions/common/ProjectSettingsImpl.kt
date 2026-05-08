package io.technoirlab.conventions.common

import io.technoirlab.conventions.common.api.ProjectSettings
import io.technoirlab.conventions.common.api.ProjectSettings.Companion.DEFAULT_GROUP_ID
import io.technoirlab.conventions.common.api.ProjectSettings.Companion.DEFAULT_VERSION
import io.technoirlab.gradle.Environment
import io.technoirlab.gradle.localGradleProperty
import org.gradle.api.Project
import org.gradle.api.provider.Provider

internal class ProjectSettingsImpl(private val project: Project, private val environment: Environment) : ProjectSettings {
    override val groupId: Provider<String>
        get() = project.localGradleProperty("project.groupId").orElse(DEFAULT_GROUP_ID)

    override val version: Provider<String>
        get() = project.localGradleProperty("project.version")
            .orElse(environment.tagName.map { it.stripPrefix() })
            .orElse(DEFAULT_VERSION)

    private fun String.stripPrefix(): String =
        if (matches(REGEX_DOTTED_VERSION_TAG)) substring(1) else this

    private companion object {
        private val REGEX_DOTTED_VERSION_TAG = Regex("^v\\d+\\..+$")
    }
}
