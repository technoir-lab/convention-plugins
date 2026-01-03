plugins {
    id("io.technoirlab.conventions.gradle-plugin")
}

gradlePluginConfig {
    buildFeatures {
        abiValidation = true
    }
}

dependencies {
    implementation(project(":common-conventions"))
    implementation(libs.dokka.gradle.plugin)

    functionalTestImplementation(project(":libraries:gradle-test-kit"))
    functionalTestImplementation(libs.assertj.core)
}

gradlePlugin {
    plugins {
        register("rootConventions") {
            id = "io.technoirlab.conventions.root"
            implementationClass = "io.technoirlab.conventions.root.RootConventionPlugin"
        }
    }
}
