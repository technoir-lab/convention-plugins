plugins {
    id("io.technoirlab.conventions.root")
}

dependencies {
    dokka(project(":common-conventions"))
    dokka(project(":gradle-plugin-conventions"))
    dokka(project(":jvm-conventions"))
    dokka(project(":kotlin-multiplatform-conventions"))
    dokka(project(":libraries:gradle-extensions"))
    dokka(project(":libraries:gradle-test-kit"))
    dokka(project(":root-conventions"))
    dokka(project(":settings-conventions"))

    nmcpAggregation(project(":common-conventions"))
    nmcpAggregation(project(":gradle-plugin-conventions"))
    nmcpAggregation(project(":jvm-conventions"))
    nmcpAggregation(project(":kotlin-multiplatform-conventions"))
    nmcpAggregation(project(":libraries:gradle-extensions"))
    nmcpAggregation(project(":libraries:gradle-test-kit"))
    nmcpAggregation(project(":root-conventions"))
    nmcpAggregation(project(":settings-conventions"))
}
