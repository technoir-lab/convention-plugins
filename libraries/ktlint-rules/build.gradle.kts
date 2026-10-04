plugins {
    id("io.technoirlab.conventions.jvm-library")
}

jvmLibrary {
    buildFeatures {
        abiValidation = true
    }
}

dependencies {
    compileOnly(libs.ec4j.core)
    compileOnly(libs.kotlin.compiler.embeddable)
    compileOnly(libs.ktlint.cli.ruleset.core)
    compileOnly(libs.ktlint.rule.engine.core)

    testImplementation(libs.assertj.core)
    testImplementation(libs.ec4j.core)
    testImplementation(libs.ktlint.cli.ruleset.core)
    testImplementation(libs.ktlint.rule.engine.core)
    testImplementation(libs.ktlint.test)

    testCompileOnly(libs.jetbrains.annotations)

    testRuntimeOnly(libs.kotlin.compiler.embeddable)
    testRuntimeOnly(libs.slf4j.simple)
}
