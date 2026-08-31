pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
    plugins {
        val conventionPluginsVersion = "v55"
        id("io.technoirlab.conventions.gradle-plugin") version conventionPluginsVersion
        id("io.technoirlab.conventions.jvm-application") version conventionPluginsVersion
        id("io.technoirlab.conventions.jvm-library") version conventionPluginsVersion
        id("io.technoirlab.conventions.root") version conventionPluginsVersion
        id("io.technoirlab.conventions.settings") version conventionPluginsVersion
    }
}

plugins {
    id("io.technoirlab.conventions.gradle-plugin") apply false
    id("io.technoirlab.conventions.jvm-library") apply false
    id("io.technoirlab.conventions.root") apply false
    id("io.technoirlab.conventions.settings")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

globalSettings {
    projectId = "convention-plugins"

    metadata {
        description = "Conventions as code for Gradle projects."
        developer(name = "technoir", email = "technoir.dev@gmail.com")
        license(name = "The Apache Software License, Version 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.txt")
    }
}

include(":conventions:common-conventions")
include(":conventions:gradle-plugin-conventions")
include(":conventions:jvm-conventions")
include(":conventions:kotlin-multiplatform-conventions")
include(":conventions:root-conventions")
include(":conventions:settings-conventions")
include(":libraries:core-utils")
include(":libraries:gradle-extensions")
include(":libraries:gradle-test-kit")
include(":libraries:ktlint-rules")
