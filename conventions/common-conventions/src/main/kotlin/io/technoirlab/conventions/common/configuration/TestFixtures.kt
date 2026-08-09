package io.technoirlab.conventions.common.configuration

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

fun Project.configureTestFixtures() {
    pluginManager.apply("java-test-fixtures")

    pluginManager.withPlugin("maven-publish") {
        extensions.configure(PublishingExtension::class) {
            publications.withType<MavenPublication>().configureEach {
                suppressPomMetadataWarningsFor("testFixturesApiElements")
                suppressPomMetadataWarningsFor("testFixturesRuntimeElements")
            }
        }
    }
}
