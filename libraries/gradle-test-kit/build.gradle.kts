plugins {
    id("io.technoirlab.conventions.jvm-library")
}

jvmLibrary {
    buildFeatures {
        abiValidation = true

        buildConfig {
            buildConfigField("KOTLIN_VERSION", libs.versions.kotlin)
        }
    }
}

dependencies {
    implementation(gradleTestKit())
    implementation(libs.junit.jupiter.api)
}
