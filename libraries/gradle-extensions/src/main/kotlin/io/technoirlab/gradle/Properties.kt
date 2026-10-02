package io.technoirlab.gradle

import org.gradle.api.file.FileSystemLocationProperty
import org.gradle.api.provider.HasMultipleValues
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.io.File

fun <T : Any> Property<T>.setDisallowChanges(value: T?) {
    set(value)
    disallowChanges()
}

fun <T : Any> Property<T>.setDisallowChanges(provider: Provider<out T>) {
    set(provider)
    disallowChanges()
}

fun <T : Any> HasMultipleValues<T>.setDisallowChanges(elements: Iterable<T>?) {
    set(elements)
    disallowChanges()
}

fun <T : Any> HasMultipleValues<T>.setDisallowChanges(provider: Provider<out Iterable<T>>) {
    set(provider)
    disallowChanges()
}

fun <K : Any, V : Any> MapProperty<K, V>.setDisallowChanges(value: Map<out K, V>?) {
    set(value)
    disallowChanges()
}

fun <K : Any, V : Any> MapProperty<K, V>.setDisallowChanges(provider: Provider<out Map<out K, V>>) {
    set(provider)
    disallowChanges()
}

fun FileSystemLocationProperty<*>.setDisallowChanges(file: File?) {
    set(file)
    disallowChanges()
}
