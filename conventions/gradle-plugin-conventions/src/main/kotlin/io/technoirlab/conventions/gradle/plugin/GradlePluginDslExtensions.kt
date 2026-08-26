package io.technoirlab.conventions.gradle.plugin

import io.technoirlab.conventions.gradle.plugin.configuration.API_VARIANT_NAME
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider

/**
 * Configures [dependency] to select the API of the target Gradle plugin component.
 *
 * @return the dependency targeting the API of the component
 */
@Suppress("UnstableApiUsage", "UnusedReceiverParameter")
fun DependencyHandler.apiOf(dependency: ModuleDependency): ModuleDependency = dependency.capabilities {
    requireFeature(API_VARIANT_NAME)
}

/**
 * Declares a dependency on the API of a Gradle plugin component.
 *
 * @param dependencyNotation the coordinates of the component whose API should be used
 * @return a dependency targeting the API of the component
 */
fun DependencyHandler.apiOf(dependencyNotation: String): ModuleDependency = apiOf(create(dependencyNotation) as ModuleDependency)

/**
 * Configures [dependency] to select the API of the target Gradle plugin component.
 *
 * @return a new dependency provider targeting the API of the component
 */
fun DependencyHandler.apiOf(dependency: Provider<MinimalExternalModuleDependency>): Provider<ModuleDependency> = dependency.map {
    apiOf(create(it) as ModuleDependency)
}
