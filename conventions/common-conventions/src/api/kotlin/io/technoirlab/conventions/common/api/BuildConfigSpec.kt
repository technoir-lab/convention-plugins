package io.technoirlab.conventions.common.api

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import java.io.Serializable

/**
 * Configuration of `BuildConfig` class generation.
 */
abstract class BuildConfigSpec {
    /**
     * Fields to include in the generated `BuildConfig` class.
     */
    abstract val fields: ListProperty<BuildConfigFieldSpec<out Serializable>>

    /**
     * Add a new field to include in the generated `BuildConfig` class.
     */
    inline fun <reified T : Serializable> buildConfigField(name: String, value: T?, variant: String? = null) {
        buildConfigField(name, T::class.java, value, variant)
    }

    /**
     * Add a new field to include in the generated `BuildConfig` class.
     */
    inline fun <reified T : Serializable> buildConfigField(name: String, valueProvider: Provider<T>, variant: String? = null) {
        buildConfigField(name, T::class.java, valueProvider, variant)
    }

    /**
     * Add a new field to include in the generated `BuildConfig` class.
     */
    fun <T : Serializable> buildConfigField(name: String, type: Class<T>, value: T?, variant: String? = null) {
        fields.add(BuildConfigFieldSpec(name, type, value, variant))
    }

    /**
     * Add a new field to include in the generated `BuildConfig` class.
     */
    fun <T : Serializable> buildConfigField(name: String, type: Class<T>, valueProvider: Provider<T>, variant: String? = null) {
        fields.addAll(valueProvider.map { value -> listOf(BuildConfigFieldSpec(name, type, value, variant)) }.orElse(emptyList()))
    }
}
