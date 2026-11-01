plugins {
    id("io.technoirlab.conventions.jvm-library")
}

jvmLibrary {
    buildFeatures {
        abiValidation = true

        buildConfig {
            buildConfigField("AGP_VERSION", libs.versions.android.gradle.plugin)
            buildConfigField("KOTLIN_VERSION", libs.versions.kotlin)
            buildConfigField("KSP_VERSION", libs.versions.ksp)
            buildConfigField("KTLINT_GRADLE_VERSION", libs.versions.ktlint.gradle)
        }
    }
}

dependencies {
    implementation(gradleTestKit())
    implementation(libs.junit.jupiter.api)

    compileOnly(libs.jetbrains.annotations)
}
