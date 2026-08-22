import org.gradle.util.GradleVersion

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

            if (GradleVersion.current() >= GradleVersion.version("9.6")) {
                enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")
            }
        }
    }
}
