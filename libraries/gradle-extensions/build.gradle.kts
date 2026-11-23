plugins {
    id("io.technoirlab.conventions.jvm-library")
}

jvmLibrary {
    buildFeatures {
        abiValidation = true
    }
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    testImplementation(project(":libraries:gradle-test-kit"))
    testImplementation(libs.assertj.core)
}

tasks.test {
    // ProjectBuilder needs reflective access to java.lang when injecting synthetic classes.
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
}
