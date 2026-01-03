import org.gradle.api.publish.maven.MavenPublication

plugins {
    `java-library`
    `jvm-test-suite`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.kover")
    id("com.gradleup.nmcp")
}

publishing {
    publications {
        val libraryMaven by registering(MavenPublication::class) {
            from(components["java"])
        }
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
}
