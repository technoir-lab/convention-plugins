package io.technoirlab.gradle

fun String.capitalized(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
