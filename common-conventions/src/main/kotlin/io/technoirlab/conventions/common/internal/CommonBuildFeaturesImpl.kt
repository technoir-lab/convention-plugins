package io.technoirlab.conventions.common.internal

import io.technoirlab.conventions.common.api.CommonBuildFeatures

abstract class CommonBuildFeaturesImpl : CommonBuildFeatures {
    open fun initDefaults() {
        abiValidation.convention(false)
        serialization.convention(false)
    }
}
