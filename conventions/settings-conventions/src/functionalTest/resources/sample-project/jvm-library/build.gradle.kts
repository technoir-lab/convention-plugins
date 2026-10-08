import org.gradle.api.publish.maven.MavenPublication

plugins {
    `java-library`
    `maven-publish`
}

publishing {
    publications {
        register<MavenPublication>("libraryMaven") {
            from(components["java"])
        }
    }
}
