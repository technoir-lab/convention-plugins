apply<GradleTestKitPlugin>()

class GradleTestKitPlugin : Plugin<Gradle> {
    override fun apply(gradle: Gradle) {
        gradle.beforeSettings {
            pluginManagement {
                repositories {
                    gradlePluginPortal()
                    mavenCentral()
                    google()
                    mavenLocal()
                }
                plugins {
// plugins content
                }
            }

            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                    gradlePluginPortal()
                    google()
                    mavenLocal()
                }
            }
        }
    }
}
