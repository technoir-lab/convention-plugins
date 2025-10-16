pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.github.com/technoir-lab/convention-plugins") {
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                password = providers.gradleProperty("gpr.token").orNull
            }
        }
    }
    plugins {
        val conventionPluginsVersion = "v38"
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
        maven("https://maven.pkg.github.com/technoir-lab/convention-plugins") {
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                password = providers.gradleProperty("gpr.token").orNull
            }
        }
    }
}

globalSettings {
    projectId = "convention-plugins"

    metadata {
        description = "Conventions as code for Gradle projects."
        developer(name = "technoir", email = "technoir.dev@gmail.com")
        license(name = "The Apache Software License, Version 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.txt")
    }
}

include(":common-conventions")
include(":gradle-plugin-conventions")
include(":jvm-conventions")
include(":kotlin-multiplatform-conventions")
include(":root-conventions")
include(":settings-conventions")
include(":libraries:gradle-extensions")
include(":libraries:gradle-test-kit")
