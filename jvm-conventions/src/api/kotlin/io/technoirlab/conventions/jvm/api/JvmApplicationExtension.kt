package io.technoirlab.conventions.jvm.api

import io.technoirlab.conventions.common.api.CommonExtension
import org.gradle.api.Action
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

/**
 * Configuration for JVM application projects.
 */
@JvmApplicationDsl
interface JvmApplicationExtension : CommonExtension {
    /**
     * The name of the application's main class.
     * Can be a fully qualified name or relative to the package name, e.g. `com.example.MainKt` or `.MainKt`.
     */
    val mainClass: Property<String>

    /**
     * The list of string arguments to pass to the JVM when running the application.
     */
    val jvmArgs: ListProperty<String>

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
        const val NAME = "jvmApplication"
    }
}
