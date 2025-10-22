plugins {
    id("io.technoirlab.conventions.gradle-plugin")
}

gradlePluginConfig {
    packageName = "io.technoirlab.conventions.kotlin.multiplatform"

    buildFeatures {
        abiValidation = true

        buildConfig {
            buildConfigField("KOTLIN_BOM", libs.kotlin.bom.map { it.toString() })
            buildConfigField("KOTLINX_BENCHMARK_RUNTIME", libs.kotlinx.benchmark.runtime.map { it.toString() })
            buildConfigField("KOTLINX_COROUTINES_BOM", libs.kotlinx.coroutines.bom.map { it.toString() })
            buildConfigField("KOTLINX_SERIALIZATION_BOM", libs.kotlinx.serialization.bom.map { it.toString() })
        }
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
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.kotlinx.benchmark.gradle.plugin)

    functionalTestImplementation(testFixtures(project(":common-conventions")))
    functionalTestImplementation(project(":libraries:gradle-test-kit"))
    functionalTestImplementation(libs.assertj.core)

    runtimeOnly(libs.metro.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatformApplicationConventions") {
            id = "io.technoirlab.conventions.kotlin-multiplatform-application"
            implementationClass = "io.technoirlab.conventions.kotlin.multiplatform.KotlinMultiplatformApplicationConventionPlugin"
        }
        register("kotlinMultiplatformLibraryConventions") {
            id = "io.technoirlab.conventions.kotlin-multiplatform-library"
            implementationClass = "io.technoirlab.conventions.kotlin.multiplatform.KotlinMultiplatformLibraryConventionPlugin"
        }
    }
}
