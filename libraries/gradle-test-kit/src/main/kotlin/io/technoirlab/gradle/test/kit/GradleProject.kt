package io.technoirlab.gradle.test.kit

import org.intellij.lang.annotations.Language
import java.nio.file.Path
import kotlin.io.path.appendText
import kotlin.io.path.div

class GradleProject internal constructor(
    val dir: Path,
    val rootDir: Path
) {
    fun project(name: String): GradleProject {
        require(":" !in name) { "Project name must not contain ':'" }
        return GradleProject(dir / name, rootDir)
    }
}

val GradleProject.buildDir: Path
    get() = dir / "build"

val GradleProject.buildScript: Path
    get() = dir / "build.gradle.kts"

val GradleProject.settingsScript: Path
    get() = rootDir / "settings.gradle.kts"

val GradleProject.gradleProperties: Path
    get() = dir / "gradle.properties"

fun GradleProject.appendBuildScript(@Language("kotlin") code: String): GradleProject =
    apply { buildScript.appendText(code) }

fun GradleProject.kotlinFile(className: String, variant: String = "main") =
    dir / "src/$variant/kotlin/${className.replace('.', '/')}.kt"

fun GradleProject.generatedFile(generator: Generator, className: String, variant: String = "main"): Path =
    buildDir.resolve(generator.outputPath(variant, className))
