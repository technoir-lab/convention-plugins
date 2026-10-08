package io.technoirlab.conventions.common.configuration

import io.technoirlab.gradle.dependencies.testFixturesImplementation
import org.gradle.api.Project
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named

internal fun Project.configureTestFixtures() {
    pluginManager.withPlugin("java-test-fixtures") {
        components.named<AdhocComponentWithVariants>("java") {
            withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
            withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
        }
    }
}

internal fun Project.configureTestFixtures(kotlinLibraries: Provider<KotlinLibraries>) {
    pluginManager.withPlugin("java-test-fixtures") {
        dependencies {
            testFixturesImplementation(kotlinLibraries.map { platform(it.kotlinBom) })
            testFixturesImplementation(kotlinLibraries.map { platform(it.kotlinCoroutinesBom) })
            testFixturesImplementation(kotlinLibraries.map { platform(it.kotlinSerializationBom) })
        }
    }
}
