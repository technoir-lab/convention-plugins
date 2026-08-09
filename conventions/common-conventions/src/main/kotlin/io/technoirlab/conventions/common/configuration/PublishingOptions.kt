package io.technoirlab.conventions.common.configuration

data class PublishingOptions(
    val componentName: String,
    val publicationName: String,
    val docsFormats: Set<DocsFormat> = DocsFormat.Multiplatform
)
