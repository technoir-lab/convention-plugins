plugins {
    `java-library`
    id("io.technoirlab.conventions.jvm-library")
}

jvmLibrary {
    buildFeatures {
        abiValidation = true

        buildConfig {
            buildConfigField("KOTLIN_VERSION", libs.versions.kotlin)
            buildConfigField("KTLINT_GRADLE_VERSION", libs.versions.ktlint.gradle)
        }
    }
}

dependencies {
    implementation(gradleTestKit())
    implementation(libs.junit.jupiter.api)

    compileOnly(libs.jetbrains.annotations)
}
