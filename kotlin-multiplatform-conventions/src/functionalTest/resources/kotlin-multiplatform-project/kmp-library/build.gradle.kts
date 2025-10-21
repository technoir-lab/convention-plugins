plugins {
    id("io.technoirlab.conventions.kotlin-multiplatform-library")
}

kotlinMultiplatformLibrary {
    buildFeatures {
        cinterop = true
    }
}

kotlin {
    jvm()
    linuxX64()
    macosArm64()
    mingwX64()
}
