plugins {
    id("io.technoirlab.conventions.kotlin-multiplatform-application")
    id("com.android.kotlin.multiplatform.library")
}

kotlinMultiplatformApplication {
    buildFeatures {
        cinterop = true
    }
}

kotlin {
    android {}
    jvm()
    linuxX64()
    macosArm64()
    mingwX64()

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
