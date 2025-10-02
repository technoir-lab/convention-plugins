package io.technoirlab.conventions.common.configuration

import io.technoirlab.conventions.common.api.ProjectSettings
import org.gradle.api.Project

fun Project.configureCommon(projectSettings: ProjectSettings) {
    val groupId = projectSettings.groupId.get()
    // A subproject might have the same name as the root project,
    // but they must have a unique 'group:name:version'.
    group = if (this == rootProject) "$groupId.root" else groupId
    version = projectSettings.version.get()
}
