import org.gradle.api.publish.maven.MavenPublication

plugins {
    `java-library`
    `maven-publish`
}

publishing {
    publications {
        val libraryMaven by registering(MavenPublication::class) {
            from(components["java"])
        }
    }
}
