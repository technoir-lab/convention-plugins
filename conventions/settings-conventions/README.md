Settings conventions
====================

## Usage

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        // Maven repository where the plugins are published
        maven("https://maven.pkg.github.com/technoir-lab/convention-plugins") {
            credentials(PasswordCredentials::class)
        }
    }
    plugins {
        val conventionPluginsVersion = "<version>"
        id("io.technoirlab.conventions.settings") version conventionPluginsVersion
    }
}

plugins {
    id("io.technoirlab.conventions.settings")
}

globalSettings {
    // Unique project ID. Required.
    projectId = "my-project"

    metadata {
        // The project's name.
        name = "Example project"
        // The project's description.
        description = "Example project description"
        // The project's URL.
        url = "https://example.org/example-project"

        // The project's developers.
        developer(id = "developer-1", name = "Developer 1", email = "developer-1@example.org")

        // The project's licenses.
        licence(name = "MIT License", url = "http://opensource.org/licenses/MIT", distribution = "repo")
    }
}
```
