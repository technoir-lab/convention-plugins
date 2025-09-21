plugins {
    id("io.technoirlab.conventions.gradle-plugin")
}

gradlePluginConfig {
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

    implementation(project(":gradle-extensions"))
    implementation(libs.dependency.analysis.gradle.plugin)
    implementation(libs.develocity.gradle.plugin)
    implementation(libs.nmcp.gradle.plugin)

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
