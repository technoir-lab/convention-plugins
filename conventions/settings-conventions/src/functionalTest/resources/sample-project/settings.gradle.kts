plugins {
    id("io.technoirlab.conventions.settings")
}

globalSettings {
    projectId = "sample-project"
}

enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")

include(":jvm-library")
