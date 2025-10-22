package io.technoirlab.conventions.kotlin.multiplatform.api

import io.technoirlab.conventions.common.api.CommonBuildFeatures
import org.gradle.api.provider.Property

interface KotlinMultiplatformBuildFeatures : CommonBuildFeatures {
    /**
     * Enable benchmarking. Disabled by default.
     */
    val benchmark: Property<Boolean>

    /**
     * Enable C interop. Disabled by default.
     */
    val cinterop: Property<Boolean>

    /**
     * Enable Metro dependency injection.
     */
    val metro: Property<Boolean>

    override fun initDefaults() {
        super.initDefaults()
        benchmark.convention(false)
        cinterop.convention(false)
        metro.convention(false)
    }
}
