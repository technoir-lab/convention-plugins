package io.technoirlab.conventions.common.configuration

import com.squareup.sort.SortDependenciesExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureDependencySorting() {
    pluginManager.apply("com.squareup.sort-dependencies")

    extensions.configure(SortDependenciesExtension::class) {
        check(false)
    }
}
