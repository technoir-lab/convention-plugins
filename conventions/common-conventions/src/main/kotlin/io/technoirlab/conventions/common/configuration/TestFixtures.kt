package io.technoirlab.conventions.common.configuration

import org.gradle.api.Project
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named

fun Project.configureTestFixtures() {
    pluginManager.apply("java-test-fixtures")

    components.named<AdhocComponentWithVariants>("java") {
        withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
        withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
    }
}
