package io.technoirlab.gradle.test.kit

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.copyToRecursively

internal fun copyResources(dirName: String, targetDir: Path) {
    val resourcesDir = Paths.get(::copyResources.javaClass.classLoader.getResource(dirName)!!.toURI())
    resourcesDir.copyToRecursively(targetDir, overwrite = false, followLinks = true)
}

internal fun readTextResource(fileName: String): String =
    ::readTextResource.javaClass.classLoader.getResource(fileName)!!.readText()
