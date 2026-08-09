import org.gradle.util.GradleVersion

if (GradleVersion.current() >= GradleVersion.version("9.6")) {
    enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")
}

include(":example-plugin")
