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
    testImplementation(libs.ktlint.rule.engine)
    testImplementation(libs.ktlint.test)
    testRuntimeOnly(libs.kotlin.compiler.embeddable)
    testRuntimeOnly(libs.slf4j.simple)
}
