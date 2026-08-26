import io.technoirlab.conventions.gradle.plugin.apiOf

plugins {
    id("io.technoirlab.conventions.gradle-plugin")
}

gradlePluginConfig {
    packageName = "com.example.dependent.plugin"
}

dependencies {
    implementation(apiOf(project(":example-plugin")))

    testImplementation(testFixtures(project(":example-plugin")))
}

gradlePlugin {
    plugins {
        register("dependent") {
            id = "com.example.dependent"
            implementationClass = "com.example.dependent.plugin.DependentPlugin"
        }
    }
}
