package io.technoirlab.conventions.android.internal

import io.technoirlab.conventions.android.api.AndroidLibraryBuildFeatures

internal abstract class AndroidLibraryBuildFeaturesImpl :
    AndroidBuildFeaturesImpl(),
    AndroidLibraryBuildFeatures {
    override fun initDefaults() {
        super.initDefaults()
        testFixtures.convention(false)
    }
}
