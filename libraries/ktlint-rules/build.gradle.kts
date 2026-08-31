plugins {
    id("io.technoirlab.conventions.jvm-library")
}

jvmLibrary {
    buildFeatures {
        abiValidation = true
    }
}

dependencies {
    implementation(libs.ktlint.cli.ruleset.core)
    implementation(libs.ktlint.rule.engine.core)

    testImplementation(libs.assertj.core)
    testImplementation(libs.ktlint.test)
}
