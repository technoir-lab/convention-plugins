package io.technoirlab.conventions.android.configuration

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.KotlinPlatform

internal fun Project.configureAndroidDokka() {
    val androidComponents = extensions.getByType<LibraryAndroidComponentsExtension>()
    val dokkaExtension = extensions.getByType<DokkaExtension>()

    androidComponents.onVariants(androidComponents.selector().withBuildType(RELEASE_VARIANT)) { variant ->
        dokkaExtension.dokkaSourceSets.register(variant.name) {
            analysisPlatform.set(KotlinPlatform.AndroidJVM)
            sourceRoots.from(variant.sources.java?.all, variant.sources.kotlin?.all)
            classpath.from(androidComponents.sdkComponents.bootClasspath)
            classpath.from(variant.compileClasspath)
        }
    }
}
