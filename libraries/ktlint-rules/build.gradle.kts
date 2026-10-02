plugins {
    `java-library`
    id("io.technoirlab.conventions.jvm-library")
}

jvmLibrary {
    buildFeatures {
        abiValidation = true
    }
}

dependencies {
    implementation(libs.ec4j.core)
    implementation(libs.kotlin.compiler.embeddable)
    implementation(libs.ktlint.cli.ruleset.core)
    implementation(libs.ktlint.rule.engine.core)

    testImplementation(libs.assertj.core)
    testImplementation(libs.ktlint.test)

    testCompileOnly(libs.jetbrains.annotations)

    testRuntimeOnly(libs.slf4j.simple)
}
