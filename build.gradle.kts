plugins {
    id("io.technoirlab.conventions.root")
}

dependencies {
    dokka(project(":conventions:common-conventions"))
    dokka(project(":conventions:gradle-plugin-conventions"))
    dokka(project(":conventions:jvm-conventions"))
    dokka(project(":conventions:kotlin-multiplatform-conventions"))
    dokka(project(":conventions:root-conventions"))
    dokka(project(":conventions:settings-conventions"))
    dokka(project(":libraries:gradle-extensions"))
    dokka(project(":libraries:gradle-test-kit"))

    nmcpAggregation(project(":conventions:common-conventions"))
    nmcpAggregation(project(":conventions:gradle-plugin-conventions"))
    nmcpAggregation(project(":conventions:jvm-conventions"))
    nmcpAggregation(project(":conventions:kotlin-multiplatform-conventions"))
    nmcpAggregation(project(":conventions:root-conventions"))
    nmcpAggregation(project(":conventions:settings-conventions"))
    nmcpAggregation(project(":libraries:gradle-extensions"))
    nmcpAggregation(project(":libraries:gradle-test-kit"))
}
