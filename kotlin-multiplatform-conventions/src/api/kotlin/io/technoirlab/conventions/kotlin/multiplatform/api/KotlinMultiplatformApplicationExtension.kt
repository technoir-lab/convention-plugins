package io.technoirlab.conventions.kotlin.multiplatform.api

/**
 * Configuration for Kotlin Multiplatform application projects.
 */
@KotlinMultiplatformApplicationDsl
interface KotlinMultiplatformApplicationExtension : KotlinMultiplatformExtension {
    /**
     * @suppress
     */
    companion object {
        const val NAME = "kotlinMultiplatformApplication"
    }
}
