package io.technoirlab.core

import java.nio.file.Path

operator fun Path.div(other: Collection<String>): Path = other.fold(this) { current, segment -> current.resolve(segment) }
