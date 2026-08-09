package io.technoirlab.conventions.android.configuration

import com.android.build.api.dsl.CommonExtension
import io.technoirlab.conventions.common.api.BuildConfigFieldSpec
import io.technoirlab.conventions.common.api.BuildConfigSpec

internal fun configureBuildConfig(androidExtension: CommonExtension, buildConfigSpec: BuildConfigSpec) {
    val fields = buildConfigSpec.fields.get()
    if (fields.isEmpty()) return

    androidExtension.buildFeatures.buildConfig = true
    fields.forEach { field ->
        val (type, literal) = field.toJavaTypeAndLiteral()
        val target = field.variant?.let(androidExtension.buildTypes::getByName) ?: androidExtension.defaultConfig
        target.buildConfigField(type, field.name, literal)
    }
}

private fun BuildConfigFieldSpec<*>.toJavaTypeAndLiteral(): Pair<String, String> = when (val value = value) {
    null -> {
        check(type == String::class.java) { "BuildConfig field '$name' of type '${type.name}' cannot be null" }
        "String" to "null"
    }
    is String -> "String" to value.toJavaStringLiteral()
    is Boolean -> "boolean" to "$value"
    is Int -> "int" to "$value"
    is Long -> "long" to "${value}L"
    // AGP appends 'f' to float literals unless they already end with a lowercase 'f'
    is Float -> "float" to "${value}f"
    is Double -> "double" to "$value"
    else -> error("Unsupported BuildConfig field type '${type.name}'")
}

private fun String.toJavaStringLiteral(): String = buildString {
    append('"')
    this@toJavaStringLiteral.forEach { char ->
        when (char) {
            '"', '\\' -> append('\\').append(char)
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            else -> append(char)
        }
    }
    append('"')
}
