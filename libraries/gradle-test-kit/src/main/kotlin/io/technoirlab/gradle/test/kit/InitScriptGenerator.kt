package io.technoirlab.gradle.test.kit

import gradle.test.kit.BuildConfig
import java.nio.file.Path
import kotlin.io.path.writeText

internal class InitScriptGenerator {
    @Suppress("MagicNumber")
    fun generate(initScriptFile: Path) {
        val template = readTextResource("gradle-test-kit.init.gradle.kts")
        val pluginsBlock = getPluginIds().entries.joinToString(separator = "\n") {
            "id(\"${it.key}\") version \"${it.value}\"".prependIndent(" ".repeat(4 * 5))
        }
        val content = template.replace("// plugins content", pluginsBlock)
        initScriptFile.writeText(content)
    }

    private fun getPluginIds(): Map<String, String> {
        val pluginVersion = systemProperty("gradle.test.kit.plugin.version")
        val pluginIds = systemProperty("gradle.test.kit.plugin.ids")
            .split(',')
            .filter(String::isNotEmpty)
        return pluginIds.associateWith { pluginVersion } + mapOf(
            "org.jetbrains.kotlin.jvm" to BuildConfig.KOTLIN_VERSION,
            "org.jetbrains.kotlin.multiplatform" to BuildConfig.KOTLIN_VERSION,
        )
    }

    private fun systemProperty(name: String): String =
        checkNotNull(System.getProperty(name)) { "Missing required property '$name'" }
}
