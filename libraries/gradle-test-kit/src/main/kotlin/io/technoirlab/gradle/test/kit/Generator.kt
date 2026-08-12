package io.technoirlab.gradle.test.kit

import java.nio.file.Path
import kotlin.io.path.Path

enum class Generator {
    BuildConfig,
}

internal fun Generator.outputPath(variant: String, className: String): Path = when (this) {
    Generator.BuildConfig -> Path("generated/sources/buildConfig/$variant/${className.replace('.', '/')}.kt")
}
