package io.technoirlab.conventions.kotlin.multiplatform.configuration

import io.technoirlab.conventions.kotlin.multiplatform.BuildConfig
import io.technoirlab.gradle.capitalized
import kotlinx.benchmark.gradle.BenchmarksExtension
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMetadataTarget

internal fun Project.configureBenchmarking(enable: Property<Boolean>) {
    if (!enable.get()) return

    pluginManager.apply("org.jetbrains.kotlinx.benchmark")

    val benchmarksExtension = the<BenchmarksExtension>()
    extensions.configure(KotlinMultiplatformExtension::class) {
        val benchmarkSourceSet = sourceSets.create("benchmark")
        targets.matching { it !is KotlinMetadataTarget }.configureEach {
            val mainCompilation = compilations["main"]
            val benchmarkCompilation = compilations.create("benchmark") {
                associateWith(mainCompilation)
                defaultSourceSet {
                    dependsOn(benchmarkSourceSet)
                    dependencies {
                        implementation(BuildConfig.KOTLINX_BENCHMARK_RUNTIME)
                    }
                }
            }
            benchmarksExtension.targets.register(targetName + benchmarkCompilation.name.capitalized())
        }
    }
}
