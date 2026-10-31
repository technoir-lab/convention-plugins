package io.technoirlab.conventions.settings.configuration

import androidx.build.gradle.gcpbuildcache.ExportedKeyGcpCredentials
import androidx.build.gradle.gcpbuildcache.GcpBuildCache
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.remote
import java.io.File

internal fun Settings.configureBuildCache() {
    val projectId = providers.environmentVariable("GCP_BUILD_CACHE_PROJECT_ID").orNull
    val bucketName = providers.environmentVariable("GCP_BUILD_CACHE_BUCKET_NAME").orNull
    val credentialsPath = providers.environmentVariable("GCP_BUILD_CACHE_CREDENTIALS").orNull
    val isRemoteCacheEnabled = projectId != null && bucketName != null && credentialsPath != null
    val isRemoteCachePushEnabled = isRemoteCacheEnabled &&
        providers.environmentVariable("GCP_BUILD_CACHE_PUSH")
            .map(String::toBoolean)
            .getOrElse(false)

    buildCache {
        if (isRemoteCachePushEnabled) {
            local {
                isEnabled = false
            }
        }
        if (isRemoteCacheEnabled) {
            remote<GcpBuildCache> {
                this.projectId = projectId
                this.bucketName = bucketName
                credentials = ExportedKeyGcpCredentials(File(credentialsPath))
                isPush = isRemoteCachePushEnabled
            }
        }
    }
}
