package io.technoirlab.conventions.kotlin.multiplatform.api

/**
 * Configuration for Kotlin Multiplatform library projects.
 */
@KotlinMultiplatformLibraryDsl
interface KotlinMultiplatformLibraryExtension : KotlinMultiplatformExtension {
    /**
     * @suppress
     */
    companion object {
        const val NAME = "kotlinMultiplatformLibrary"
    }
}
