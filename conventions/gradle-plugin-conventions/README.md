Gradle plugin conventions
=========================

## Usage

```kotlin
plugins {
    id("io.technoirlab.conventions.gradle-plugin")
}

gradlePluginConfig {
    // The base package name
    packageName = "com.example.gradle.plugin"

    // Optional build features
    buildFeatures {
        // Disable ABI validation. Enabled by default.
        abiValidation = false
        // Enable `toString()` redaction. Disabled by default.
        redacted = true
        // Enable Kotlin serialization. Disabled by default.
        serialization = true

        // Configuration of `BuildConfig` class generation
        buildConfig {
            // Add a String field
            buildConfigField("STRING_FIELD", "string value")
            // Add a variant-specific field
            buildConfigField("TEST_STRING_FIELD", "string value", variant = "test")
        }
    }
}
```
