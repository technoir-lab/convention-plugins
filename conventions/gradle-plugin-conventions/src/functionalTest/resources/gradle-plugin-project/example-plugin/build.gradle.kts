plugins {
    id("io.technoirlab.conventions.gradle-plugin")
}

gradlePluginConfig {
    packageName = "com.example.plugin"
}

gradlePlugin {
    plugins {
        register("example") {
            id = "com.example.plugin"
            implementationClass = "com.example.plugin.ExamplePlugin"
        }
    }
}
