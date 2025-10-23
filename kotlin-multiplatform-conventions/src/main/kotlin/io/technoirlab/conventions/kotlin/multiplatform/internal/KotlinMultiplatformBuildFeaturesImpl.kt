package io.technoirlab.conventions.kotlin.multiplatform.internal

import io.technoirlab.conventions.common.internal.CommonBuildFeaturesImpl
import io.technoirlab.conventions.kotlin.multiplatform.api.KotlinMultiplatformBuildFeatures

internal abstract class KotlinMultiplatformBuildFeaturesImpl : CommonBuildFeaturesImpl(), KotlinMultiplatformBuildFeatures {
    override fun initDefaults() {
        super.initDefaults()
        benchmark.convention(false)
        cinterop.convention(false)
        metro.convention(false)
    }
}
