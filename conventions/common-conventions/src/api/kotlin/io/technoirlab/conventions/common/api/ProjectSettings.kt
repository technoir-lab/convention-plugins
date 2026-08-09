package io.technoirlab.conventions.common.api

import org.gradle.api.provider.Provider

interface ProjectSettings {
    val groupId: Provider<String>
    val version: Provider<String>

    companion object {
        const val DEFAULT_GROUP_ID = "io.technoirlab"
        const val DEFAULT_VERSION = "dev"
    }
}
