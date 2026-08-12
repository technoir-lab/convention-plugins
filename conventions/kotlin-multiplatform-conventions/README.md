Kotlin multiplatform conventions
================================

## KMP Application

```kotlin
plugins {
    id("io.technoirlab.conventions.kotlin-multiplatform-application")
}

kotlinMultiplatformApplication {
    // The base package name
    packageName = "com.example.kmp.application"

    // Optional build features
    buildFeatures {
        // Enable ABI validation. Disabled by default.
        abiValidation = true
        // Enable benchmarking. Disabled by default.
        benchmark = true
        // Enable Metro dependency injection. Disabled by default.
        metro = true
        // Enable `toString()` redaction. Disabled by default.
        redacted = true
        // Enable Kotlin serialization. Disabled by default.
        serialization = true
        // Enable C interop. Disabled by default.
        cinterop = true

        // Configuration of `BuildConfig` class generation
        buildConfig {
            // Add a String field
            buildConfigField("STRING_FIELD", "string value")
            // Add a variant-specific field
            buildConfigField("TEST_STRING_FIELD", "string value", variant = "test")
        }
    }
}

kotlin {
    // Define the targets
    jvm()
    ...
}
```

## KMP Library

```kotlin
plugins {
    id("io.technoirlab.conventions.kotlin-multiplatform-library")
}

kotlinLibrary {
    // The base package name
    packageName = "com.example.kmp.library"

    // Optional build features
    buildFeatures {
        // Enable ABI validation. Disabled by default.
        abiValidation = true
        // Enable benchmarking. Disabled by default.
        benchmark = true
        // Enable Metro dependency injection. Disabled by default.
        metro = true
        // Enable `toString()` redaction. Disabled by default.
        redacted = true
        // Enable Kotlin serialization. Disabled by default.
        serialization = true
        // Enable C interop. Disabled by default.
        cinterop = true

        // Configuration of `BuildConfig` class generation
        buildConfig {
            // Add a String field
            buildConfigField("STRING_FIELD", "string value")
            // Add a variant-specific field
            buildConfigField("TEST_STRING_FIELD", "string value", variant = "test")
        }
    }
}

kotlin {
    // Define the targets
    jvm()
    ...
}
```
