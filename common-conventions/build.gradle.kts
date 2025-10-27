plugins {
    id("io.technoirlab.conventions.gradle-plugin")
}

gradlePluginConfig {
    packageName = "io.technoirlab.conventions.common"

    buildFeatures {
        abiValidation = true

        buildConfig {
            buildConfigField("GROUP_ID", provider { "$group" })
            buildConfigField("VERSION", provider { "$version" })
            buildConfigField("JUNIT5_VERSION", libs.versions.junit5)
            buildConfigField("KOTLIN_VERSION", libs.versions.kotlin)
            buildConfigField("KOTLINX_COROUTINES_VERSION", libs.versions.kotlinx.coroutines)
            buildConfigField("KOTLINX_SERIALIZATION_VERSION", libs.versions.kotlinx.serialization)
        }
    }
}

dependencies {
    implementation(project(":libraries:gradle-extensions"))
    implementation(libs.buildconfig.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.kotlin.sam.with.receiver.gradle.plugin)
    implementation(libs.maven.artifact)
    implementation(libs.sort.dependencies.gradle.plugin)

    functionalTestImplementation(project(":libraries:gradle-test-kit"))
    functionalTestImplementation(libs.assertj.core)

    runtimeOnly(libs.kotlin.serialization.gradle.plugin)
    runtimeOnly(libs.nmcp.gradle.plugin)

    testFixturesImplementation(project(":libraries:gradle-test-kit"))

    testImplementation(libs.assertj.core)
}

gradlePlugin {
    plugins {
        register("commonConventions") {
            id = "io.technoirlab.conventions.common"
            implementationClass = "io.technoirlab.conventions.common.CommonConventionPlugin"
        }
    }
}
