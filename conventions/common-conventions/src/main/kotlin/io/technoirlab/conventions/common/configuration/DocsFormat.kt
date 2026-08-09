package io.technoirlab.conventions.common.configuration

enum class DocsFormat(val classifier: String) {
    Html("html-docs"),
    Javadoc("javadoc");

    companion object {
        val All = setOf(Html, Javadoc)
        val Multiplatform = setOf(Html)
    }
}
