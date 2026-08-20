@file:Suppress("NOTHING_TO_INLINE")

package io.technoirlab.gradle

import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import java.nio.file.Path

inline fun Directory.asPath(): Path = asFile.toPath()

inline fun RegularFile.asPath(): Path = asFile.toPath()
