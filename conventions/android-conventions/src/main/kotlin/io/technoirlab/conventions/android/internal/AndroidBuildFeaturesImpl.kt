package io.technoirlab.conventions.android.internal

import io.technoirlab.conventions.android.api.AndroidBuildFeatures
import io.technoirlab.conventions.common.internal.CommonBuildFeaturesImpl

internal abstract class AndroidBuildFeaturesImpl :
    CommonBuildFeaturesImpl(),
    AndroidBuildFeatures {
    override fun initDefaults() {
        super.initDefaults()
        parcelize.convention(false)
    }
}
