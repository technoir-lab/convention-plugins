package io.technoirlab.conventions.common.fixtures

import io.technoirlab.gradle.test.kit.GradleProject
import io.technoirlab.gradle.test.kit.kotlinFile
import org.intellij.lang.annotations.Language
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

fun GradleProject.createDependencyGraph(className: String = "com.example.AppGraph") = apply {
    val packageName = className.substringBeforeLast('.')
    val classNameWithoutPackage = className.substringAfterLast('.')
    kotlinFile(className, variant = "commonMain")
        .createParentDirectories()
        .writeText(
            // language=kotlin
            """
            package $packageName
            
            import dev.zacsweers.metro.AppScope
            import dev.zacsweers.metro.DependencyGraph
            import dev.zacsweers.metro.Provides
            import dev.zacsweers.metro.createGraph
            
            @DependencyGraph(AppScope::class)
            interface $classNameWithoutPackage {
                @Provides
                fun provideString(): String = "hello"
            }
            
            val graph = createGraph<AppGraph>()
            """.trimIndent(),
        )
}

fun GradleProject.createBenchmark(className: String = "com.example.Benchmark") = apply {
    val packageName = className.substringBeforeLast('.')
    val classNameWithoutPackage = className.substringAfterLast('.')
    kotlinFile(className, variant = "benchmark")
        .createParentDirectories()
        .writeText(
            // language=kotlin
            """
            package $packageName
            
            import kotlin.random.Random
            import kotlinx.benchmark.Benchmark
            import kotlinx.benchmark.Blackhole
            import kotlinx.benchmark.Measurement
            import kotlinx.benchmark.Scope
            import kotlinx.benchmark.State
            import kotlinx.benchmark.Warmup
            
            @Warmup(iterations = 1)
            @Measurement(iterations = 1)
            @State(Scope.Benchmark)
            open class $classNameWithoutPackage {
                @Benchmark
                fun benchmark(blackhole: Blackhole) {
                    blackhole.consume(Random.nextInt())
                }
            }
            """.trimIndent(),
        )
}

fun GradleProject.createRedactedFixtures(
    className: String = "com.example.RedactedClass",
    variant: String = "main",
    testVariant: String = "test",
) {
    val packageName = className.substringBeforeLast('.')
    val classNameWithoutPackage = className.substringAfterLast('.')
    kotlinFile(className, variant)
        .createParentDirectories()
        .writeText(
            // language=kotlin
            """
            package $packageName
            
            import dev.zacsweers.redacted.annotations.Redacted
            
            internal data class $classNameWithoutPackage(
                @Redacted val secret: String,
                val nonSecret: String,
            )
            """.trimIndent(),
        )
    kotlinFile(className, variant = testVariant)
        .createParentDirectories()
        .writeText(
            // language=kotlin
            """
            package $packageName
            
            import kotlin.test.Test
            import kotlin.test.assertEquals
            
            class ${classNameWithoutPackage}Test {
                @Test
                fun `redacts secrets from string representation`() {
                    assertEquals(
                        "$classNameWithoutPackage(secret=██, nonSecret=not a secret)",
                        $classNameWithoutPackage("secret", "not a secret").toString(),
                    )
                }
            }
            """.trimIndent(),
        )
}

fun GradleProject.createSerializableClass(className: String = "com.example.SerializableClass", variant: String = "main") = apply {
    val packageName = className.substringBeforeLast('.')
    val classNameWithoutPackage = className.substringAfterLast('.')
    kotlinFile(className, variant)
        .createParentDirectories()
        .writeText(
            // language=kotlin
            """
            package $packageName
            
            import kotlinx.serialization.Serializable
            
            @Serializable
            internal data class $classNameWithoutPackage(
                val name: String,
                val count: Int,
            )
            
            internal val serializer = $classNameWithoutPackage.serializer()
            """.trimIndent(),
        )
}

fun GradleProject.createParcelableClass(className: String = "com.example.ParcelableClass") = apply {
    val packageName = className.substringBeforeLast('.')
    val classNameWithoutPackage = className.substringAfterLast('.')
    kotlinFile(className)
        .createParentDirectories()
        .writeText(
            // language=kotlin
            """
            package $packageName
            
            import android.os.Parcelable
            import kotlinx.parcelize.Parcelize
            
            @Parcelize
            internal data class $classNameWithoutPackage(
                val name: String,
                val count: Int,
            ) : Parcelable
            """.trimIndent(),
        )
}

fun GradleProject.createTestFixture(className: String = "com.example.TestFixture") = apply {
    val packageName = className.substringBeforeLast('.')
    val classNameWithoutPackage = className.substringAfterLast('.')
    kotlinFile(className, variant = "testFixtures")
        .createParentDirectories()
        .writeText(
            // language=kotlin
            """
            package $packageName
            
            class $classNameWithoutPackage(val name: String)
            """.trimIndent(),
        )
}

fun GradleProject.createTest(
    @Language("kotlin")
    body: String,
    className: String = "com.example.ExampleTest",
) = apply {
    val packageName = className.substringBeforeLast('.')
    val classNameWithoutPackage = className.substringAfterLast('.')
    kotlinFile(className, variant = "test")
        .createParentDirectories()
        .writeText(
            // language=kotlin
            """
            |package $packageName
            |
            |import org.junit.jupiter.api.Test
            |
            |class $classNameWithoutPackage {
            |    @Test
            |    fun test() {
            |${body.prependIndent("        ")}
            |    }
            |}
            """.trimMargin(),
        )
}
