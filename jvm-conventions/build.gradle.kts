plugins {
    id("io.technoirlab.conventions.gradle-plugin")
}

gradlePluginConfig {
    packageName = "io.technoirlab.conventions.jvm"

    buildFeatures {
        abiValidation = true
    }
}

dependencies {
    apiApi(project(":common-conventions")) {
        capabilities {
            requireCapability("${project.group}:common-conventions-api")
        }
    }

    implementation(project(":common-conventions"))
    implementation(project(":libraries:gradle-extensions"))

    functionalTestImplementation(testFixtures(project(":common-conventions")))
    functionalTestImplementation(project(":libraries:gradle-test-kit"))
    functionalTestImplementation(libs.assertj.core)
}

gradlePlugin {
    plugins {
        register("jvmApplicationConventions") {
            id = "io.technoirlab.conventions.jvm-application"
            implementationClass = "io.technoirlab.conventions.jvm.JvmApplicationConventionPlugin"
        }
        register("jvmLibraryConventions") {
            id = "io.technoirlab.conventions.jvm-library"
            implementationClass = "io.technoirlab.conventions.jvm.JvmLibraryConventionPlugin"
        }
    }
}
