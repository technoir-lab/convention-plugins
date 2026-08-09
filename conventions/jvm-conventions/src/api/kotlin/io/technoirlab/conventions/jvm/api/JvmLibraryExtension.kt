package io.technoirlab.conventions.jvm.api

import io.technoirlab.conventions.common.api.CommonExtension
import org.gradle.api.Action
import org.gradle.api.tasks.Nested

/**
 * Configuration for JVM library projects.
 */
@JvmLibraryDsl
interface JvmLibraryExtension : CommonExtension {
    /**
     * Optional build features.
     */
    @get:Nested
    override val buildFeatures: JvmBuildFeatures

    /**
     * Optional build features.
     */
    fun buildFeatures(action: Action<JvmBuildFeatures>) {
        action.execute(buildFeatures)
    }

    /**
     * @suppress
     */
    companion object {
        const val NAME = "jvmLibrary"
    }
}
