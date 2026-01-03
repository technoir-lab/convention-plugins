package io.technoirlab.conventions.common.configuration

import io.technoirlab.conventions.common.api.metadata.DeveloperInfo
import io.technoirlab.conventions.common.api.metadata.LicenseInfo
import io.technoirlab.conventions.common.api.metadata.ProjectMetadata
import io.technoirlab.gradle.Environment
import io.technoirlab.gradle.maven
import io.technoirlab.gradle.setDisallowChanges
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension

fun Project.configurePublishing(
    options: PublishingOptions,
    metadata: ProjectMetadata,
    environment: Environment,
    extraConfiguration: MavenPublication.() -> Unit = {}
) {
    pluginManager.apply("maven-publish")

    extensions.configure(PublishingExtension::class) {
        publications.withType<MavenPublication>().configureEach {
            versionMapping {
                usage(Usage.JAVA_API) {
                    fromResolutionResult()
                }
                usage(Usage.JAVA_RUNTIME) {
                    fromResolutionResult()
                }
            }

            if (name == options.publicationName) {
                options.docsFormats.forEach { docsFormat ->
                    artifact(dokkaJar(docsFormat))
                }
                extraConfiguration()
            }

            pom.configure(metadata, environment, artifactId)
        }

        repositories {
            val publishRepositories = environment.getMavenRepositories("publish").get()
            publishRepositories.forEach { maven(it) }
        }

        configureSigning(publications)
    }

    afterEvaluate {
        extensions.configure(PublishingExtension::class) {
            publications {
                if (options.publicationName !in names) {
                    register(options.publicationName, MavenPublication::class) {
                        from(components[options.componentName])
                    }
                }

                withType<MavenPublication>().configureEach {
                    pom.configure(metadata.licenses.get(), metadata.developers.get())
                }
            }
        }
    }
}

private fun Project.configureSigning(publications: PublicationContainer) {
    pluginManager.apply("signing")

    extensions.configure(SigningExtension::class) {
        val secretKey = providers.environmentVariable("SIGNING_KEY")
        val password = providers.environmentVariable("SIGNING_PASSWORD")
        useInMemoryPgpKeys(secretKey.orNull, password.orNull)
        sign(publications)
        isRequired = false
    }
}

@Suppress("NoNameShadowing", "UnusedPrivateMember") // false positive
private fun MavenPom.configure(metadata: ProjectMetadata, environment: Environment, artifactId: String) {
    name.set(metadata.name.orElse(artifactId))
    url.set(metadata.url.orElse(environment.repositoryUrl.map { it.toString() }))
    description.set(metadata.description)

    scm {
        url.setDisallowChanges(environment.repositoryUrl.map { it.toString() })
        connection.setDisallowChanges(environment.vcsUrl.map { "scm:git:$it" })
        developerConnection.setDisallowChanges(environment.vcsUrl.map { "scm:git:$it" })
    }
}

@Suppress("UnusedPrivateMember") // false positive
private fun MavenPom.configure(licenses: List<LicenseInfo>, developers: List<DeveloperInfo>) {
    licenses {
        licenses.forEach { license ->
            license {
                name.setDisallowChanges(license.name)
                url.setDisallowChanges(license.url)
            }
        }
    }

    developers {
        developers.forEach { developer ->
            developer {
                id.setDisallowChanges(developer.id)
                name.setDisallowChanges(developer.name)
                email.setDisallowChanges(developer.email)
                organization.setDisallowChanges(developer.organization)
                organizationUrl.setDisallowChanges(developer.organizationUrl)
            }
        }
    }
}
