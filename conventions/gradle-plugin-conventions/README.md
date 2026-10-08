Gradle plugin conventions
=========================

See [common conventions](../common-conventions/README.md#kotlin-compiler-options) for shared Kotlin compiler options.

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

## Kotlin compatibility

`gradlePluginConfig.minGradleVersion` determines Kotlin compatibility. The `api` source set uses
the Kotlin language and API level of Gradle's Kotlin DSL build scripts, limited to the compiler's
default level.

Implementation and test source sets use two separate limits:

- The API level is the lower of the embedded runtime's Kotlin version and the compiler's default level.
- The language level is the lower of the next Kotlin language version after the embedded runtime's
  version (for example, 2.4 after 2.3) and the compiler's default level.

Kotlin core libraries remain aligned with the minimum Gradle version's embedded runtime.

For example, targeting Gradle 9.6 allows Kotlin 2.4 syntax with Kotlin 2.3 APIs in implementation
code and Kotlin 2.2 in `api`, with Kotlin 2.3.21 core libraries. With a compiler default of Kotlin 2.4,
targeting Gradle 9.8 sets both implementation levels to 2.4: the language level does not advance to 2.5.
Kotlin 2.4 library functions such as `isSorted()` require a minimum Gradle version of 9.7 or later,
even when building on a newer Gradle version.
