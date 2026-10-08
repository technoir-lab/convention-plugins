import io.technoirlab.conventions.gradle.plugin.apiOf

plugins {
    `java-library`
    id("io.technoirlab.conventions.gradle-plugin")
}

gradlePluginConfig {
    packageName = "io.technoirlab.conventions.android"

    buildFeatures {
        buildConfig {
            buildConfigField("DESUGAR_JDK_LIBS", libs.desugar.jdk.libs.map { it.toString() })
            buildConfigField("JUNIT_VERSION", libs.versions.junit)
        }
    }
}

dependencies {
    apiApi(apiOf(project(":conventions:common-conventions")))

    implementation(project(":conventions:common-conventions"))
    implementation(project(":libraries:gradle-extensions"))
    implementation(libs.android.gradle.api)
    implementation(libs.dokka.gradle.plugin)

    functionalTestImplementation(testFixtures(project(":conventions:common-conventions")))
    functionalTestImplementation(project(":libraries:gradle-test-kit"))
    functionalTestImplementation(libs.assertj.core)

    runtimeOnly(libs.android.gradle.plugin)

    testImplementation(project(":libraries:gradle-test-kit"))

    testRuntimeOnly(libs.android.gradle.settings.plugin)

    functionalTestPublishOnly(project(":libraries:ktlint-rules"))
}

gradlePlugin {
    plugins {
        register("androidApplicationConventions") {
            id = "io.technoirlab.conventions.android-application"
            implementationClass = "io.technoirlab.conventions.android.AndroidApplicationConventionPlugin"
        }
        register("androidLibraryConventions") {
            id = "io.technoirlab.conventions.android-library"
            implementationClass = "io.technoirlab.conventions.android.AndroidLibraryConventionPlugin"
        }
    }
}
