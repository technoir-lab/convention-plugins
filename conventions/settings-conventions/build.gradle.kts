import io.technoirlab.conventions.gradle.plugin.apiOf

plugins {
    id("io.technoirlab.conventions.gradle-plugin")
}

dependencies {
    apiApi(apiOf(project(":conventions:common-conventions")))

    implementation(project(":libraries:gradle-extensions"))
    implementation(libs.dependency.analysis.gradle.plugin)
    implementation(libs.develocity.gradle.plugin)
    implementation(libs.nmcp.gradle.plugin)

    functionalTestImplementation(project(":libraries:gradle-test-kit"))
    functionalTestImplementation(libs.assertj.core)

    runtimeOnly(libs.foojay.resolver.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("settingsConventions") {
            id = "io.technoirlab.conventions.settings"
            implementationClass = "io.technoirlab.conventions.settings.SettingsConventionPlugin"
        }
    }
}
