plugins {
    id("com.android.kotlin.multiplatform.library") apply false
    id("io.technoirlab.conventions.kotlin-multiplatform-application") apply false
    id("io.technoirlab.conventions.kotlin-multiplatform-library") apply false
}

include(":kmp-application")
include(":kmp-library")
