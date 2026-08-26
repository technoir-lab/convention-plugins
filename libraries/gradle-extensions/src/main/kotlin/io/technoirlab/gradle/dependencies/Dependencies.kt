package io.technoirlab.gradle.dependencies

import org.gradle.api.artifacts.Dependency
import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.api(dependencyNotation: Any): Dependency? = "api"(dependencyNotation)

fun DependencyHandlerScope.compileOnly(dependencyNotation: Any): Dependency? = "compileOnly"(dependencyNotation)

fun DependencyHandlerScope.implementation(dependencyNotation: Any): Dependency? = "implementation"(dependencyNotation)
