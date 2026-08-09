package io.technoirlab.conventions.common.configuration

import com.github.gmazzo.buildconfig.BuildConfigExtension
import io.technoirlab.conventions.common.api.BuildConfigSpec
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.configure

fun Project.configureBuildConfig(buildConfigSpec: BuildConfigSpec, packageName: Property<String>) {
    val buildConfigFields = buildConfigSpec.fields.get()
    if (buildConfigFields.isEmpty()) return

    pluginManager.apply("com.github.gmazzo.buildconfig")

    extensions.configure(BuildConfigExtension::class) {
        this.packageName.set(packageName)

        buildConfigFields.filter { field -> field.variant == null }.forEach { field ->
            buildConfigField(field.type, field.name, field.value)
        }

        sourceSets.configureEach {
            buildConfigFields.filter { field -> field.variant == name }.forEach { field ->
                buildConfigField(field.type, field.name, field.value)
            }
        }
    }
}
