package io.technoirlab.conventions.gradle.plugin.configuration

import io.technoirlab.conventions.common.configuration.configureTestSuite
import io.technoirlab.conventions.gradle.plugin.api.GradlePluginExtension
import io.technoirlab.gradle.Environment
import io.technoirlab.gradle.dependencies.api
import io.technoirlab.gradle.setDisallowChanges
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.gradleKotlinDsl
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugin.devel.tasks.ValidatePlugins
import org.gradle.process.CommandLineArgumentProvider
import org.gradle.testing.base.TestingExtension
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.tasks.BaseKotlinCompile

internal fun Project.configurePlugin(config: GradlePluginExtension, environment: Environment) {
    configurations.dependencyScope("${FUNCTIONAL_TEST_VARIANT_NAME}PublishOnly")

    configureApiVariant(API_VARIANT_NAME)

    @Suppress("UnstableApiUsage")
    extensions.configure(TestingExtension::class) {
        val functionalTestSuite = suites.register(FUNCTIONAL_TEST_VARIANT_NAME, JvmTestSuite::class) {
            configureTestSuite {
                configureFunctionalTestTask(config)
            }
            dependencies {
                implementation.add(project())
                implementation.add(gradleTestKit())
            }
        }

        tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).configure {
            dependsOn(functionalTestSuite)
        }

        extensions.configure(GradlePluginDevelopmentExtension::class) {
            website.setDisallowChanges(config.metadata.url)
            vcsUrl.setDisallowChanges(environment.vcsUrl)

            plugins.configureEach {
                displayName = config.metadata.name.orNull
                description = config.metadata.description.orNull
            }

            testSourceSet(functionalTestSuite.get().sources)
        }
    }

    extensions.configure(KotlinJvmExtension::class) {
        val mainCompilation = target.compilations.named(KotlinCompilation.MAIN_COMPILATION_NAME)
        target.compilations.named(FUNCTIONAL_TEST_VARIANT_NAME) {
            compileTaskProvider.configure {
                (this as BaseKotlinCompile).friendPaths.from(mainCompilation.map { it.output.classesDirs })
            }
        }
    }

    tasks.withType<ValidatePlugins>().configureEach {
        enableStricterValidation.set(true)
    }

    dependencies {
        "compileOnly"(gradleKotlinDsl())
    }
}

private fun Project.configureApiVariant(variantName: String) {
    extensions.configure(JavaPluginExtension::class) {
        val apiSourceSet = sourceSets.create(variantName)

        registerFeature(variantName) {
            withSourcesJar()
            usingSourceSet(apiSourceSet)
            capability("$group", "$name-$variantName", "$version")
        }

        if (pluginManager.hasPlugin("org.jetbrains.dokka")) {
            extensions.configure(DokkaExtension::class) {
                dokkaSourceSets.named("main") {
                    sourceRoots.from(apiSourceSet.allSource.srcDirs)
                    classpath.from(apiSourceSet.compileClasspath)
                }
            }
        }
    }

    dependencies {
        "${variantName}Implementation"(gradleApi())
        api(project(path, configuration = null)) {
            capabilities {
                requireCapability("$group:$name-$variantName")
            }
        }
    }
}

private fun Test.configureFunctionalTestTask(config: GradlePluginExtension) {
    val plugins = project.the<GradlePluginDevelopmentExtension>().plugins
    jvmArgumentProviders.add(
        GradleTestKitPropertiesArgumentProvider(
            pluginIds = project.provider { plugins.map { it.id } },
            pluginVersion = project.provider { "${project.version}" },
            minGradleVersion = config.minGradleVersion,
        ),
    )
    dependsOn(project.tasks.named("publishToMavenLocal"))

    DEPENDENCY_CONFIGURATIONS.forEach { configurationName ->
        project.configurations.named(configurationName).configure {
            dependencies.withType<ProjectDependency>().configureEach {
                dependsOn("$path:publishToMavenLocal")
            }
        }
    }
}

private class GradleTestKitPropertiesArgumentProvider(
    @get:Input val pluginIds: Provider<List<String>>,
    @get:Input val pluginVersion: Provider<String>,
    @get:Input val minGradleVersion: Provider<String>,
) : CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> = listOf(
        "-D${GradleTestKitProperties.PLUGIN_IDS}=${pluginIds.get().joinToString(",")}",
        "-D${GradleTestKitProperties.PLUGIN_VERSION}=${pluginVersion.get()}",
        "-D${GradleTestKitProperties.MIN_GRADLE_VERSION}=${minGradleVersion.get()}",
    )
}

private const val API_VARIANT_NAME = "api"
private const val FUNCTIONAL_TEST_VARIANT_NAME = "functionalTest"
private val DEPENDENCY_CONFIGURATIONS = listOf(
    "implementation",
    "api",
    "runtimeOnly",
    "${API_VARIANT_NAME}Api",
    "${API_VARIANT_NAME}Implementation",
    "${FUNCTIONAL_TEST_VARIANT_NAME}PublishOnly",
)
