package io.technoirlab.gradle.test.kit

import java.nio.file.Path
import java.util.jar.JarFile
import java.util.zip.ZipEntry
import kotlin.io.path.readText
import kotlin.io.path.writeText

operator fun Path.div(other: Collection<String>): Path = other.fold(this) { current, segment -> current.resolve(segment) }

fun Path.replaceText(oldText: String, newText: String) {
    require(oldText != newText) { "oldText and newText must be different" }

    val content = readText()
    val newContent = content.replaceFirst(oldText, newText)
    check(content != newContent) { "Text '$oldText' not found in file '$this'" }
    writeText(newContent)
}

fun Path.jarEntries(): List<String> = JarFile(toFile()).use { jarFile -> jarFile.entries().asSequence().map(ZipEntry::getName).toList() }
